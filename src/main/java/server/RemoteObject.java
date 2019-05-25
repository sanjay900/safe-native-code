package server;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.UUID;

public class RemoteObject implements Serializable {
    private UUID uuid;
    private JVM remote;

    public RemoteObject(JVM remote) {
        this.uuid = UUID.randomUUID();
        this.remote = remote;
    }

    /**
     * Call a method on a remote object.
     * Note that arguments are treated as primitives. If this is not desirable, use getRemoteMethod
     * @param methodName the method to call
     * @param args the arguments to pass to the method
     * @return
     * @throws RemoteException a problem occurred while talking to the remote jvm
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public RemoteObject call(String methodName, Object... args) throws RemoteException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return remote.call(this, methodName, args);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Object get() throws RemoteException {
        return remote.get(this);
    }

    public RemoteObject move(JVM jvm) throws RemoteException {
        return remote.move(this, jvm);
    }
    public RemoteObject move(ServerJVM jvm) throws RemoteException {
        return move(jvm.getClient());
    }
}
