package slave.process;

import slave.RemoteObject;
import slave.types.SlaveType;
import utils.function.Consumer;
import utils.function.Function;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

/**
 * SlaveProcessObject represents an object on a slave
 */
public class SlaveProcessObject<T> implements RemoteObject<T> {
    private UUID uuid;
    private SlaveProcess slave;

    SlaveProcessObject(SlaveProcess remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    public <R> RemoteObject<R> call(Function<R, T> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }


    public void run(Consumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }


    public RemoteObject<T> copyTo(SlaveType slave) throws RemoteException {
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
