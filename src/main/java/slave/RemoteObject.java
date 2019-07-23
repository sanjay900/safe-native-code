package slave;

import server.Slave;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

/**
 * RemoteObject represents an object on the slave, and exposes an API for interacting with it
 */
public class RemoteObject<T> implements Serializable {
    private UUID uuid;
    private ISlaveMain slave;

    RemoteObject(ISlaveMain remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    /**
     * Execute a function on the slave, expecting a return value
     *
     * @param lambda the function to execute
     * @return the returned data from the executed function
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    public <R> RemoteObject<R> call(SerializableFunction<T, R> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }

    /**
     * Execute a function on the slave, expecting no return values
     *
     * @param lambda the function to execute
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    public void run(SerializableConsumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }

    /**
     * Move this remote object to another slave
     *
     * @param slave the slave to move this object to
     * @return this remote object, for chaining
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    public RemoteObject<T> move(Slave slave) throws RemoteException {
        this.slave.move(this, slave.getRemoteSlaveJVM());
        this.slave = slave.getRemoteSlaveJVM();
        return this;
    }

    /**
     * Move this remote object to another slave
     *
     * @param slave the slave to move this object to
     * @return the new remote object on slave
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    public RemoteObject<T> copy(Slave slave) throws RemoteException {
        return this.slave.copy(this, slave.getRemoteSlaveJVM());
    }

    /**
     * Get the value this remote object represents, serializing it in the process
     *
     * @return the value this remote object represents
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    public T get() throws RemoteException {
        return slave.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteObject<?> that = (RemoteObject<?>) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
