package slaveProcess;

import slave.slaves.Slave;
import shared.RemoteObject;
import shared.SerializableConsumer;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

/**
 * SlaveProcessObject represents an object on a slave
 */
public class SlaveProcessObject<T> implements RemoteObject<T> {
    private UUID uuid;
    private shared.Slave slave;

    SlaveProcessObject(shared.Slave remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    public <R> RemoteObject<R> call(Slave.One<R, T> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }


    public void run(SerializableConsumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }


    public RemoteObject<T> copy(Slave slave) throws RemoteException {
        return slave.copy(this);
    }

    public T get() throws RemoteException {
        return slave.get(this);
    }

    @Override
    public void remove() throws RemoteException {
        slave.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlaveProcessObject<?> that = (SlaveProcessObject<?>) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    protected void finalize() throws RemoteException {
        this.remove();
    }
}
