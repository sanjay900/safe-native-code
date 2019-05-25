import org.junit.Assert;
import org.junit.Test;
import server.Slave;
import slave.RemoteObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;

public class Tests {
    @Test
    public void basicTest() throws InterruptedException, NotBoundException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Slave slave = new Slave(System.getProperty("java.class.path"));
        RemoteObject<Adder> c = slave.newInst(Adder.class);
        Assert.assertEquals(c.call(t -> t.calculateNumber(5, 6)).get(), 11, 1);
    }

    @Test
    public void copyObject() throws InterruptedException, NotBoundException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Slave slave = new Slave(System.getProperty("java.class.path"));
        Slave slave2 = new Slave(System.getProperty("java.class.path"));
        RemoteObject<LocalAdder> c = slave.newInst(LocalAdder.class);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
        RemoteObject<LocalAdder> c2 = c.copy(slave2);
        c2.run(s -> s.setBase(15));
        Assert.assertEquals(c2.call(t -> t.addToBase(5)).get(), 20, 0);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
    }

    @Test
    public void moveObject() throws InterruptedException, NotBoundException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Slave slave = new Slave(System.getProperty("java.class.path"));
        Slave slave2 = new Slave(System.getProperty("java.class.path"));
        RemoteObject<LocalAdder> c = slave.newInst(LocalAdder.class);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 15, 0);
        RemoteObject<LocalAdder> c2 = c.move(slave2);
        c2.run(s -> s.setBase(15));
        Assert.assertEquals(c2.call(t -> t.addToBase(5)).get(), 20, 0);
        Assert.assertEquals(c.call(t -> t.addToBase(5)).get(), 20, 0);
    }
}
