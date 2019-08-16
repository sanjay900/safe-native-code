import compiler.JavaCompiler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import server.backends.*;
import shared.IncorrectSlaveException;
import shared.RemoteObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
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

    private static RemoteBackend first;
    private static RemoteBackend second;

    @BeforeClass
    public static void init() throws InterruptedException, IOException {
        first = new RemoteBackend(false, JavaCompiler.getClassLoader());
        second = new RemoteBackend(false, JavaCompiler.getClassLoader());
    }

    @Test
    public void basicTest() throws IOException {
        RemoteObject<Adder> c = first.call(Adder::new);
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
    public void multiTest() throws IOException {
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
    public void testIncorrectRemoteBackend() throws IOException {
        //Start two RemoteBackends, and then try to call a function on another RemoteBackend
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
    public void copyObject() throws IOException {
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
        Map<Backend, Long> timings = new HashMap<>();
        List<Class<? extends Backend>> backendsClasses = new ArrayList<>();
        backendsClasses.add(DirectBackend.class);
        backendsClasses.add(RemoteBackend.class);
        backendsClasses.add(DockerBackend.class);
        backendsClasses.add(VagrantBackend.class);
        List<Backend> backends = new ArrayList<>();
        ClassLoader[] loaders = new ClassLoader[]{JavaCompiler.getClassLoader()};
        for (Class<? extends Backend> clazz : backendsClasses) {
            System.out.println("Constructing " + clazz.getName());
            Instant start = Instant.now();
            try {
                backends.add(clazz.getDeclaredConstructor(boolean.class, ClassLoader[].class).newInstance(false, loaders));
            } catch (NoSuchMethodException ex) {
                backends.add(clazz.getDeclaredConstructor().newInstance());
            }
            Instant end = Instant.now();
            System.out.println("Time taken to start " + clazz.getName() + ": " + Duration.between(start, end).toMillis());
        }
        int testCount = 20;
        for (Backend backend : backends) {
            for (int i = 0; i < testCount + 5; i++) {
                Instant start = Instant.now();
                RemoteObject<TimingTest> test = backend.call(TimingTest::new);
                test.run(TimingTest::addAll);
                TimingTest t = test.get();
                Instant end = Instant.now();
                Assert.assertEquals(expected, t.local, 0);
                if (i > 5) {
                    timings.put(backend, timings.getOrDefault(backend, 0L) + Duration.between(start, end).toMillis());
                }
            }
        }
        for (Backend backend : backends) {
            System.out.println("Time taken for " + backend.getClass().getName() + " :" + timings.get(backend) / testCount);
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void TestDynamicCompilation() throws Exception {
        Class<?> clazz = JavaCompiler.compile(
                "public class Test {" +
                        "public String getData() {return \"test\";}" +
                        "}", "Test");
        Assert.assertEquals("test", first.call(() -> {
            try {
                assert clazz != null;
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
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

    @Test
    public void TestStopping() throws Exception {
        System.out.println("Constructing backends");
        Backend[] backends = new Backend[]{
                new RemoteBackend(false, JavaCompiler.getClassLoader()),
                new DockerBackend(false, JavaCompiler.getClassLoader()),
                new VagrantBackend(false, JavaCompiler.getClassLoader()),
        };

        for (Backend backend : backends) {
            String name = backend.getClass().getName();
            System.out.println("Stopping: " + name);
            backend.exit(0);
            //We have sent the stop command, but something like docker is going to take a bit to stop.
            Thread.sleep(1000);
            Assert.assertFalse(backend.isAlive());
            System.out.println("Stopped: " + name);
        }
    }

    @Test(expected = UnmarshalException.class)
    @SuppressWarnings("deprecation")
    public void TestSerialisation() throws Exception {

        Class<?> clazz = JavaCompiler.compile(
                "public class Test implements java.io.Serializable {" +
                        "public String getData() {return \"test\";}" +
                        "}", "Test");
        Assert.assertEquals("test", first.call(() -> {
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
