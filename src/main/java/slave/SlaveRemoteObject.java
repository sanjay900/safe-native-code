package slave;

import shared.RemoteObject;
import server.backends.Backend;
import shared.SerializableConsumer;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

/**
 * SlaveRemoteObject represents an object on the slave, and exposes an API for interacting with it
 */
public class SlaveRemoteObject<T> implements RemoteObject<T> {
    private UUID uuid;
    private Slave slave;

    SlaveRemoteObject(Slave remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    public <R> RemoteObject<R> call(Backend.One<R, T> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }


    public void run(SerializableConsumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }


    public RemoteObject<T> copy(Backend slave) throws RemoteException {
        return slave.copy(this);
    }

    public T get() throws RemoteException {
        return slave.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaveRemoteObject<?> that = (SlaveRemoteObject<?>) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
