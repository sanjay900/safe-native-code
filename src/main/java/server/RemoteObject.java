package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

public class RemoteObject<T> implements Serializable {
    private UUID uuid;
    private JVM remote;

    public RemoteObject(JVM remote) {
        this.uuid = UUID.randomUUID();
        this.remote = remote;
    }


    public <R> RemoteObject<R> call(SerializableFunction<T,R> lambda) throws RemoteException {
        return remote.call(this, lambda);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Object get() throws RemoteException {
        return remote.get(this);
    }

    public RemoteObject<T> move(JVM jvm) throws RemoteException {
        return remote.move(this, jvm);
    }
    public RemoteObject<T> move(ServerJVM jvm) throws RemoteException {
        return move(jvm.getClient());
    }
}
