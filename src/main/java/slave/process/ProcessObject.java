package slave.process;

import slave.RemoteObject;
import slave.SlaveInternal;
import slave.Functions;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

/**
 * ProcessObject represents an object on a slave
 */
public class ProcessObject<T> implements RemoteObject<T> {
    private UUID uuid;
    private Process slave;

    ProcessObject(Process remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    public <R> RemoteObject<R> call(Functions.Function<R, T> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }


    public void run(Functions.Consumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }


    public RemoteObject<T> copyTo(SlaveInternal slave) throws RemoteException {
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

    @SuppressWarnings("deprecation")
    protected void finalize() throws RemoteException {
        this.remove();
    }
}
