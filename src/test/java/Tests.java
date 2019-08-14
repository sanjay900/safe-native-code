import compiler.JavaCompiler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import server.SafeCodeLibrary;
import server.RemoteObject;
import server.backends.RemoteBackend;
import slave.IncorrectSlaveException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;
import java.time.Duration;
import java.time.Instant;

public class Tests {
    @BeforeClass
    public static void initialize() {
        SafeCodeLibrary.initialiseWithClassLoaders(1234, 1235, JavaCompiler.getClassLoader());
    }

    static class Adder implements Serializable {
        int calculateNumber(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void basicTest() throws InterruptedException, NotBoundException, IOException {
        RemoteBackend RemoteBackend = new RemoteBackend();
        RemoteObject<Adder> c = RemoteBackend.call(Adder::new);
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
    public void multiTest() throws InterruptedException, NotBoundException, IOException {
        RemoteBackend RemoteBackend = new RemoteBackend();
        RemoteObject<A> a1 = RemoteBackend.call(() -> new A(3));
        RemoteObject<A> a2 = RemoteBackend.call(() -> new A(5));
        RemoteObject<A> a3 = RemoteBackend.call(a1, a2, A::another);
        A _a2 = new A(3);
        RemoteObject<A> a323 = a1.call(la1 -> la1.another(_a2));
        RemoteObject<A> a32 = RemoteBackend.call(a1, a323, A::another);
        Assert.assertEquals(a3.call(s -> s.f).get(), 8, 0);
        Assert.assertEquals(a32.call(s -> s.f).get(), 9, 0);
    }

    @Test(expected = IncorrectSlaveException.class)
    public void testIncorrectRemoteBackend() throws InterruptedException, NotBoundException, IOException {
        //Start two RemoteBackends, and then try to call a function on another RemoteBackend
        RemoteBackend RemoteBackend = new RemoteBackend();
        RemoteBackend RemoteBackend2 = new RemoteBackend();
        RemoteObject<LocalAdder> c = RemoteBackend.call(LocalAdder::new);
        RemoteBackend2.call(c, c2 -> c2);
    }

    static class LocalAdder implements Serializable {
        private int base = 10;

        int addToBase(int integer) {
            return integer + base;
        }

        void setBase(int base) {
            this.base = base;
        }

        int getBase() {
            return base;
        }
    }


    @Test
    public void copyObject() throws InterruptedException, NotBoundException, IOException {
        RemoteBackend RemoteBackend = new RemoteBackend();
        RemoteBackend RemoteBackend2 = new RemoteBackend();
        RemoteObject<LocalAdder> c = RemoteBackend.call(LocalAdder::new);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
        RemoteObject<LocalAdder> c2 = c.copy(RemoteBackend2);
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
    public void time() throws InterruptedException, NotBoundException, IOException {
        long expected = 49999995000000L;
        int timeMillisDirect = 0;
        int timeMillisRemoteBackend = 0;
        for (int i = 0; i < 20; i++) {
            Instant start = Instant.now();
            TimingTest t = new TimingTest();
            t.addAll();
            Instant end = Instant.now();
            Assert.assertEquals(expected, t.local, 0);
            timeMillisDirect += Duration.between(start, end).toMillis();
            start = Instant.now();
            RemoteBackend RemoteBackend = new RemoteBackend();
            RemoteObject<TimingTest> test = RemoteBackend.call(TimingTest::new);
            test.run(TimingTest::addAll);
            t = test.get();
            end = Instant.now();
            Assert.assertEquals(expected, t.local, 0);
            timeMillisRemoteBackend += Duration.between(start, end).toMillis();
        }
        System.out.println("Time taken for direct: " + timeMillisDirect / 20 + " milliseconds");
        System.out.println("Time taken for RemoteBackend: " + timeMillisRemoteBackend / 20 + " milliseconds");
    }

    @Test
    public void TestDynamicCompilation() throws Exception {
        Class<?> clazz = JavaCompiler.compile(
                "public class Test {" +
                        "public String getData() {return \"test\";}" +
                        "}", "Test");
        RemoteBackend RemoteBackend = new RemoteBackend();
        Assert.assertEquals("test", RemoteBackend.call(() -> {
            try {
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
}
