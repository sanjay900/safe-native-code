import compiler.JavaCompiler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import server.SafeCodeLibrary;
import server.servers.DirectServer;
import server.servers.DockerServer;
import server.servers.ProcessServer;
import server.servers.Server;
import shared.IncorrectSlaveException;
import shared.RemoteObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tests {
    static class Adder implements Serializable {
        int calculateNumber(int a, int b) {
            return a + b;
        }
    }

    @BeforeClass
    public static void init() {
        SafeCodeLibrary.secure();
    }

    @Test
    public void basicTest() throws IOException, InterruptedException {
        RemoteObject<Adder> c = new ProcessServer(JavaCompiler.getClassLoader()).call(Adder::new);
        RemoteObject<Integer> i = c.call(a -> a.calculateNumber(5, 6));
        Assert.assertEquals(i.get(), 11, 1);
    }

    static class A implements Serializable {
        int f;

        A(int f) {
            this.f = f;
        }

        A another(A a) {
            return new A(this.f + a.f);
        }
    }

    @Test
    public void multiTest() throws IOException, InterruptedException {
        ProcessServer first = new ProcessServer(JavaCompiler.getClassLoader());
        RemoteObject<A> a1 = first.call(() -> new A(3));
        RemoteObject<A> a2 = first.call(() -> new A(5));
        RemoteObject<A> a3 = first.call(a1, a2, A::another);
        A _a2 = new A(3);
        RemoteObject<A> a323 = a1.call(la1 -> la1.another(_a2));
        RemoteObject<A> a32 = first.call(a1, a323, A::another);
        Assert.assertEquals(a3.call(s -> s.f).get(), 8, 0);
        Assert.assertEquals(a32.call(s -> s.f).get(), 9, 0);
    }

    @Test(expected = IncorrectSlaveException.class)
    public void testIncorrectRemoteBackend() throws IOException, InterruptedException {
        ProcessServer first = new ProcessServer(JavaCompiler.getClassLoader());
        ProcessServer second = new ProcessServer(JavaCompiler.getClassLoader());
        //Start two RemoteBackends, and then try to call a function on another ProcessServer
        RemoteObject<LocalAdder> c = first.call(LocalAdder::new);
        second.call(c, c2 -> c2);
    }

    static class LocalAdder implements Serializable {
        private int base = 10;

        int addToBase(int integer) {
            return integer + base;
        }

        void setBase(int base) {
            this.base = base;
        }
    }


    @Test
    public void copyObject() throws IOException, InterruptedException {
        ProcessServer first = new ProcessServer(JavaCompiler.getClassLoader());
        ProcessServer second = new ProcessServer(JavaCompiler.getClassLoader());
        RemoteObject<LocalAdder> c = first.call(LocalAdder::new);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
        RemoteObject<LocalAdder> c2 = c.copy(second);
        c2.run(s -> s.setBase(15));
        //Since we copied the object, the original object should not be modified in the process.
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
        Assert.assertEquals(c2.call(t -> t.addToBase(5)).get(), 20, 0);
    }

    public static class TimingTest implements Serializable {
        long local = 0;

        void addAll() {
            for (long i = 0; i < 10000000; i++) {
                addSingle(i);
            }
        }

        void addSingle(long i) {
            local += i;
        }
    }

    @Test
    public void time() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        long expected = 49999995000000L;
        Map<Server, Long> timings = new HashMap<>();
        List<Class<? extends Server>> backendsClasses = new ArrayList<>();
        backendsClasses.add(DirectServer.class);
        backendsClasses.add(ProcessServer.class);
        backendsClasses.add(DockerServer.class);
        List<Server> servers = new ArrayList<>();
        ClassLoader[] loaders = new ClassLoader[]{JavaCompiler.getClassLoader()};
        for (Class<? extends Server> clazz : backendsClasses) {
            System.out.println("Constructing " + clazz.getName());
            Instant start = Instant.now();
            servers.add(clazz.getDeclaredConstructor(ClassLoader[].class).newInstance((Object) loaders));
            Instant end = Instant.now();
            System.out.println("Time taken to start " + clazz.getName() + ": " + Duration.between(start, end).toMillis());
        }
        int testCount = 20;
        for (Server server : servers) {
            for (int i = 0; i < testCount + 5; i++) {
                Instant start = Instant.now();
                RemoteObject<TimingTest> test = server.call(TimingTest::new);
                test.run(TimingTest::addAll);
                TimingTest t = test.get();
                Instant end = Instant.now();
                Assert.assertEquals(expected, t.local, 0);
                if (i > 5) {
                    timings.put(server, timings.getOrDefault(server, 0L) + Duration.between(start, end).toMillis());
                }
            }
        }
        for (Server server : servers) {
            System.out.println("Time taken for " + server.getClass().getName() + " :" + timings.get(server) / testCount);
        }
    }

    @Test
    public void TestDynamicCompilation() throws Exception {
        Server[] servers = new Server[]{
                new DirectServer(JavaCompiler.getClassLoader()),
                new ProcessServer(JavaCompiler.getClassLoader()),
                new DockerServer(JavaCompiler.getClassLoader()),
        };
        Class<?> clazz = JavaCompiler.compile(
                "public class Test {" +
                        "public String getData() {return \"test\";}" +
                        "}", "Test");
        for (Server server : servers) {
            System.out.println(server.getClass().getName());
            Assert.assertEquals("test", server.call(() -> {
                try {
                    assert clazz != null;
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            }).call(s -> {
                try {
                    return s.getClass().getDeclaredMethod("getData").invoke(s);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return null;
            }).get());
        }
    }

    @Test
    public void TestStopping() throws Exception {
        System.out.println("Constructing servers");
        Server[] servers = new Server[]{
                new ProcessServer(JavaCompiler.getClassLoader()),
                new DockerServer(JavaCompiler.getClassLoader()),
        };

        for (Server server : servers) {
            String name = server.getClass().getName();
            System.out.println("Stopping: " + name);
            server.terminate();
            Assert.assertFalse(server.isAlive());
            System.out.println("Stopped: " + name);
        }
    }

    @Test
    public void TestCrashing() throws Exception {
        System.out.println("Constructing servers");
        Server[] servers = new Server[]{
                new ProcessServer(JavaCompiler.getClassLoader()),
                new DockerServer(JavaCompiler.getClassLoader()),
        };

        //Simulate a process crash with System.exit
        for (Server server : servers) {
            String name = server.getClass().getName();
            System.out.println("Crashing: " + name);
            try {
                server.call(() -> System.exit(1));
            } catch (RemoteException ignored) {
                //We expect a RemoteException here, as RMI will lose its connection to the slave
            }
            server.waitForExit();
            Assert.assertFalse(server.isAlive());
            System.out.println("Stopped: " + name);
        }
    }

    @Test
    public void TestKilling() throws Exception {
        System.out.println("Constructing servers");
        Server[] servers = new Server[]{
                new ProcessServer(JavaCompiler.getClassLoader()),
                new DockerServer(JavaCompiler.getClassLoader()),
        };

        for (Server server : servers) {
            String name = server.getClass().getName();
            System.out.println("Killing: " + name);
            server.terminate();
            Assert.assertFalse(server.isAlive());
            System.out.println("Stopped: " + name);
        }
    }

    @Test(expected = UnmarshalException.class)
    @SuppressWarnings("deprecation")
    public void TestSerialisation() throws IOException, InterruptedException {

        Class<?> clazz = JavaCompiler.compile(
                "public class Test implements java.io.Serializable {" +
                        "public String getData() {return \"test\";}" +
                        "}", "Test");
        Assert.assertEquals("test", new ProcessServer(JavaCompiler.getClassLoader()).call(() -> {
            try {
                assert clazz != null;
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).get());
    }
}
