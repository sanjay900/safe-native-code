import org.junit.Assert;
import org.junit.Test;
import server.Slave;
import slave.IncorrectSlaveException;
import slave.RemoteObject;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;

public class Tests {
    static class Adder implements Serializable {
        int calculateNumber(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void basicTest() throws InterruptedException, NotBoundException, IOException {
        Slave slave = new Slave(System.getProperty("java.class.path"));
        RemoteObject<Adder> c = slave.call(Adder::new);
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
        Slave slave = new Slave(System.getProperty("java.class.path"));
        RemoteObject<A> a1 = slave.call(() -> new A(3));
        RemoteObject<A> a2 = slave.call(() -> new A(5));
        RemoteObject<A> a3 = slave.call(a1, a2, A::another);
        A _a2 = new A(3);
        RemoteObject<A> a323 = a1.call(la1 -> la1.another(_a2));
        RemoteObject<A> a32 = slave.call(a1, a323, A::another);
        Assert.assertEquals(a3.call(s -> s.f).get(), 8, 0);
        Assert.assertEquals(a32.call(s -> s.f).get(), 9, 0);
    }

    @Test(expected = IncorrectSlaveException.class)
    public void testIncorrectSlave() throws InterruptedException, NotBoundException, IOException {
        //Start two slaves, and then try to call a function on another slave
        Slave slave = new Slave(System.getProperty("java.class.path"));
        Slave slave2 = new Slave(System.getProperty("java.class.path"));
        RemoteObject<LocalAdder> c = slave.call(LocalAdder::new);
        slave2.call(c, c2 -> c2);
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
    public void copyObject() throws InterruptedException, NotBoundException, IOException {
        Slave slave = new Slave(System.getProperty("java.class.path"));
        Slave slave2 = new Slave(System.getProperty("java.class.path"));
        RemoteObject<LocalAdder> c = slave.call(LocalAdder::new);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
        RemoteObject<LocalAdder> c2 = c.copy(slave2);
        c2.run(s -> s.setBase(15));
        //Since we copied the object, the original object should not be modified in the process.
        Assert.assertEquals(c2.call(t -> t.addToBase(5)).get(), 20, 0);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
    }

    @Test
    public void moveObject() throws InterruptedException, NotBoundException, IOException {
        Slave slave = new Slave(System.getProperty("java.class.path"));
        Slave slave2 = new Slave(System.getProperty("java.class.path"));
        RemoteObject<LocalAdder> c = slave.call(LocalAdder::new);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
        RemoteObject<LocalAdder> c2 = c.move(slave2);
        c2.run(s -> s.setBase(15));
        //We moved the object, so the original object should represent the new object.
        Assert.assertEquals(c2.call(t -> t.addToBase(5)).get(), 20, 0);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 20, 0);
    }
}
