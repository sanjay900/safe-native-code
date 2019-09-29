package safeNativeCode.slave.process;

import safeNativeCode.slave.Functions;
import safeNativeCode.slave.RemoteObject;
import safeNativeCode.slave.Slave;
import safeNativeCode.slave.InternalSlave;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

/**
 * ProcessObject represents an object on a safeNativeCode.slave
 */
public class ProcessObject<T> implements RemoteObject<T> {
    private UUID uuid;
    private InternalSlave slave;

    ProcessObject(InternalSlave remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    public <R> RemoteObject<R> call(Functions.Function<R, T> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }


    public void run(Functions.Consumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }


    public RemoteObject<T> copyTo(Slave slave) throws RemoteException {
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
        ProcessObject<?> that = (ProcessObject<?>) o;
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
