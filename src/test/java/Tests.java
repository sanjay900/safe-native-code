import compiler.JavaCompiler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import shared.exceptions.IncorrectSlaveException;
import shared.RemoteObject;
import shared.SafeCodeLibrary;
import slave.slaves.DirectSlave;
import slave.slaves.DockerSlave;
import slave.slaves.ProcessSlave;
import slave.slaves.Slave;

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
        RemoteObject<Adder> c = new ProcessSlave(JavaCompiler.getClassLoader()).call(Adder::new);
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
        ProcessSlave first = new ProcessSlave(JavaCompiler.getClassLoader());
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
        ProcessSlave first = new ProcessSlave(JavaCompiler.getClassLoader());
        ProcessSlave second = new ProcessSlave(JavaCompiler.getClassLoader());
        //Start two RemoteBackends, and then try to call a function on another ProcessSlave
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
        ProcessSlave first = new ProcessSlave(JavaCompiler.getClassLoader());
        ProcessSlave second = new ProcessSlave(JavaCompiler.getClassLoader());
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
        Map<Slave, Long> timings = new HashMap<>();
        List<Class<? extends Slave>> backendsClasses = new ArrayList<>();
        backendsClasses.add(DirectSlave.class);
        backendsClasses.add(ProcessSlave.class);
        backendsClasses.add(DockerSlave.class);
        List<Slave> slaves = new ArrayList<>();
        ClassLoader[] loaders = new ClassLoader[]{JavaCompiler.getClassLoader()};
        for (Class<? extends Slave> clazz : backendsClasses) {
            System.out.println("Constructing " + clazz.getName());
            Instant start = Instant.now();
            slaves.add(clazz.getDeclaredConstructor(ClassLoader[].class).newInstance((Object) loaders));
            Instant end = Instant.now();
            System.out.println("Time taken to start " + clazz.getName() + ": " + Duration.between(start, end).toMillis());
        }
        int testCount = 20;
        for (Slave slave : slaves) {
            for (int i = 0; i < testCount + 5; i++) {
                Instant start = Instant.now();
                RemoteObject<TimingTest> test = slave.call(TimingTest::new);
                test.run(TimingTest::addAll);
                TimingTest t = test.get();
                Instant end = Instant.now();
                Assert.assertEquals(expected, t.local, 0);
                if (i > 5) {
                    timings.put(slave, timings.getOrDefault(slave, 0L) + Duration.between(start, end).toMillis());
                }
            }
        }
        for (Slave slave : slaves) {
            System.out.println("Time taken for " + slave.getClass().getName() + " :" + timings.get(slave) / testCount);
        }
    }

    @Test
    public void TestDynamicCompilation() throws Exception {
        Slave[] slaves = new Slave[]{
                new DirectSlave(JavaCompiler.getClassLoader()),
                new ProcessSlave(JavaCompiler.getClassLoader()),
                new DockerSlave(JavaCompiler.getClassLoader()),
        };
        Class<?> clazz = JavaCompiler.compile(
                "public class Test {" +
                        "public String getData() {return \"test\";}" +
                        "}", "Test");
        for (Slave slave : slaves) {
            System.out.println(slave.getClass().getName());
            Assert.assertEquals("test", slave.call(() -> {
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
        System.out.println("Constructing slaves");
        Slave[] slaves = new Slave[]{
                new ProcessSlave(JavaCompiler.getClassLoader()),
                new DockerSlave(JavaCompiler.getClassLoader()),
        };

        for (Slave slave : slaves) {
            String name = slave.getClass().getName();
            System.out.println("Stopping: " + name);
            slave.terminate();
            Assert.assertFalse(slave.isAlive());
            System.out.println("Stopped: " + name);
        }
    }

    @Test
    public void TestCrashing() throws Exception {
        System.out.println("Constructing slaves");
        Slave[] slaves = new Slave[]{
                new ProcessSlave(JavaCompiler.getClassLoader()),
                new DockerSlave(JavaCompiler.getClassLoader()),
        };

        //Simulate a process crash with System.exit
        for (Slave slave : slaves) {
            String name = slave.getClass().getName();
            System.out.println("Crashing: " + name);
            try {
                slave.call(() -> System.exit(1));
            } catch (RemoteException ignored) {
                //We expect a RemoteException here, as RMI will lose its connection to the slave
            }
            slave.waitForExit();
            Assert.assertFalse(slave.isAlive());
            System.out.println("Stopped: " + name);
        }
    }

    @Test
    public void TestKilling() throws Exception {
        System.out.println("Constructing slaves");
        Slave[] slaves = new Slave[]{
                new ProcessSlave(JavaCompiler.getClassLoader()),
                new DockerSlave(JavaCompiler.getClassLoader()),
        };

        for (Slave slave : slaves) {
            String name = slave.getClass().getName();
            System.out.println("Killing: " + name);
            slave.terminate();
            Assert.assertFalse(slave.isAlive());
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
        Assert.assertEquals("test", new ProcessSlave(JavaCompiler.getClassLoader()).call(() -> {
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
