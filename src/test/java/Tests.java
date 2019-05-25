import org.junit.Assert;
import org.junit.Test;
import server.RemoteObject;
import server.ServerJVM;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;

public class Tests {
    @Test
    public void basicTest() throws InterruptedException, NotBoundException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ServerJVM slave = new ServerJVM(System.getProperty("java.class.path"));
        RemoteObject c = slave.newInst(Adder.class);
        Assert.assertEquals(c.call("calculateNumber", 5, 6).get(), 11);
    }
    @Test
    public void copyObject() throws InterruptedException, NotBoundException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ServerJVM slave = new ServerJVM(System.getProperty("java.class.path"));
        ServerJVM slave2 = new ServerJVM(System.getProperty("java.class.path"));
        RemoteObject c = slave.newInst(Adder.class);
        Assert.assertEquals(c.call("calculateNumber", 5, 6).get(), 11);
        Assert.assertEquals(c.move(slave2).call("calculateNumber", 6, 6).get(), 12);

    }
}
