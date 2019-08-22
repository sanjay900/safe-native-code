package slave;

import server.servers.Server;
import shared.RemoteObject;
import shared.SerializableConsumer;
import shared.Slave;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

/**
 * SlaveObject represents an object on a slave, and exposes an API for interacting with it
 */
public class SlaveObject<T> implements RemoteObject<T> {
    private UUID uuid;
    private Slave slave;

    SlaveObject(Slave remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    public <R> RemoteObject<R> call(Server.One<R, T> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }


    public void run(SerializableConsumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }


    public RemoteObject<T> copy(Server slave) throws RemoteException {
        return slave.copy(this);
    }

    public T get() throws RemoteException {
        return slave.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaveObject<?> that = (SlaveObject<?>) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
