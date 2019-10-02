import compiler.JavaCompiler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import safeNativeCode.exceptions.ClassLoadingDisabledException;
import safeNativeCode.exceptions.SlaveException;
import safeNativeCode.exceptions.UnknownObjectException;
import safeNativeCode.slave.RemoteObject;
import safeNativeCode.slave.Slave;
import safeNativeCode.slave.host.DockerSlave;
import safeNativeCode.slave.host.ProcessSlave;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.UnmarshalException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class Tests {
    private static String DYNAMIC_CODE = "public class Test implements java.io.Serializable {String getData() {return \"test\";}}";
    private Class<? extends Slave> clazz;

    @SuppressWarnings("unused")
    public Tests(Class<? extends Slave> clazz, String name) {
        this.clazz = clazz;
    }

    @Parameterized.Parameters(name = "SlaveType: {1}")
    public static Iterable<Object[]> data() {
        return Stream
                .of(ProcessSlave.class, DockerSlave.class)
                .map(s -> new Object[]{s, s.getSimpleName()})
                .collect(Collectors.toList());
    }

    private Slave construct(ClassLoader... loaders) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (loaders.length == 0) {
            loaders = new ClassLoader[]{JavaCompiler.getClassLoader()};
        }
        return clazz.getDeclaredConstructor(int.class, String[].class, ClassLoader[].class).newInstance(0, new String[]{}, (Object) loaders);
    }

    private Slave construct(int timeLimit, int memLimit, ClassLoader... loaders) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (loaders.length == 0) {
            loaders = new ClassLoader[]{JavaCompiler.getClassLoader()};
        }
        return clazz.getDeclaredConstructor(int.class, String[].class, ClassLoader[].class).newInstance(timeLimit, memLimit > 0 ? new String[]{"-Xmx" + memLimit + "M"} : new String[]{}, (Object) loaders);
    }

    static class Adder implements Serializable {
        int calculateNumber(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void testCallAndGet() throws Exception {
        RemoteObject<Adder> c = construct().call(Adder::new);
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
    public void testMultipleObjects() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Slave slave = construct();
        RemoteObject<A> aWith3 = slave.call(() -> new A(3));
        RemoteObject<A> aWith5 = slave.call(() -> new A(5));
        RemoteObject<A> aWith3Plus5 = slave.call(aWith3, aWith5, A::another);
        A localAWith3 = new A(3);
        RemoteObject<A> aWith3PlusLocal3 = aWith3.call(lAWith3 -> lAWith3.another(localAWith3));
        RemoteObject<A> aWith3Plus3PlusLocal3 = slave.call(aWith3, aWith3PlusLocal3, A::another);
        Assert.assertEquals(aWith3Plus5.call(s -> s.f).get(), 8, 0);
        Assert.assertEquals(aWith3Plus3PlusLocal3.call(s -> s.f).get(), 9, 0);
    }

    @Test(expected = UnknownObjectException.class)
    public void testIncorrectSlave() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Slave first = construct();
        Slave second = construct();
        //Start two Slaves, and then try to use an object with an incorrect safeNativeCode.slave
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
    public void testCopyObject() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Slave first = construct();
        Slave second = construct();
        RemoteObject<LocalAdder> c = first.call(LocalAdder::new);
        Assert.assertEquals(15, c.call(adder -> adder.addToBase(5)).get(), 0);
        RemoteObject<LocalAdder> c2 = second.copy(c);
        c2.run(adder -> adder.setBase(15));
        //Since we copied the object, the original object should not be modified in the process.
        Assert.assertEquals(15, c.call(adder -> adder.addToBase(5)).get(), 0);
        Assert.assertEquals(20, c2.call(adder -> adder.addToBase(5)).get(), 0);
    }

    static class TimingTest implements Serializable {
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
    public void timeConstruction() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int testCount = 20;
        long totalTime = 0L;
        for (int i = 0; i < testCount + 5; i++) {
            Instant start = Instant.now();
            construct();
            Instant end = Instant.now();
            if (i > 5) {
                totalTime += Duration.between(start, end).toMillis();
            }
        }
        System.out.println("Time taken to construct: " + totalTime / testCount);
    }

    @Test
    public void timeExecution() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int testCount = 10;
        long expected = 49999995000000L;
        long totalTime = 0L;
        Slave slave = construct();
        for (int i = 0; i < testCount + 5; i++) {
            Instant start = Instant.now();
            RemoteObject<TimingTest> test = slave.call(TimingTest::new);
            test.run(TimingTest::addAll);
            TimingTest t = test.get();
            Instant end = Instant.now();
            Assert.assertEquals(expected, t.local, 0);
            if (i > 5) {
                totalTime += Duration.between(start, end).toMillis();
            }
        }
        System.out.println("Time taken to test: " + totalTime / testCount);
    }

    @Test
    public void testDynamicCompilation() throws Exception {
        JavaCompiler.compile(DYNAMIC_CODE, "Test");
        Object ret = construct()
                .call(() -> Class.forName("Test").getDeclaredConstructor().newInstance())
                .call(s -> s.getClass().getDeclaredMethod("getData").invoke(s)).get();
        Assert.assertEquals("test", ret);
    }

    @Test
    public void testStopping() throws Exception {
        Slave slave = construct();
        slave.terminate();
        Assert.assertFalse(slave.isAlive());
    }

    @Test(expected = CancellationException.class)
    public void testCrashing() throws Exception {
        construct().run(() -> System.exit(1));
    }

    @Test
    public void testKilling() throws Exception {
        Slave slave = construct();
        slave.terminate();
        Assert.assertFalse(slave.isAlive());
    }

    @Test(expected = UnmarshalException.class)
    public void testUnknownSerialisation() throws Exception {
        //We expect to not be able to retrieve this object back, as RMI has no idea that the Test object exists
        JavaCompiler.compile(DYNAMIC_CODE, "Test");
        Assert.assertEquals("test", construct().call(() -> Class.forName("Test").newInstance()).get());
    }

    @Test(expected = UnknownObjectException.class)
    public void testDeletion() throws Exception {
        Slave s = construct();
        RemoteObject<LocalAdder> la = s.call(LocalAdder::new);
        la.remove();
        la.get();
    }

    @Test(expected = ClassLoadingDisabledException.class)
    public void testSafety() throws Exception {
        Class.forName("NotRealClass");
    }

    @Test(expected = OutOfMemoryError.class)
    public void testMemoryLimit() throws Throwable {
        try {
            Slave s = construct(0, 10);
            s.call(() -> new byte[1024 * 1024 * 1024]);
        } catch (SlaveException ex) {
            throw ex.getChild().get();
        }
    }

    @Test(expected = CancellationException.class)
    public void testTimeLimit() throws Throwable {
        construct(10, 0).call(() -> {
            Thread.sleep(20000);
            return 1;
        });
    }

    private static class TestException extends Exception {

    }

    @Test(expected = SlaveException.class)
    public void testException() throws Exception {
        Slave s = construct();
        s.call(() -> {
            throw new TestException();
        });
    }

    @Test(expected = TestException.class)
    public void testExceptionAndCatch() throws Throwable {
        Slave s = construct();
        try {
            s.call(() -> {
                throw new TestException();
            });
        } catch (SlaveException ex) {
            throw ex.getChild().get();
        }
    }
}
