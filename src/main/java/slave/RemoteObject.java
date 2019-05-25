package slave;

import server.*;

import java.rmi.RemoteException;
import java.util.UUID;

public class RemoteObject<T> implements IRemoteObject<T> {
    private UUID uuid;
    private ISlaveJVM slave;

    RemoteObject(ISlaveJVM remote) {
        this.uuid = UUID.randomUUID();
        this.slave = remote;
    }

    UUID getUuid() {
        return uuid;
    }

    void setSlave(ISlaveJVM slave) {
        this.slave = slave;
    }

    public T get() throws RemoteException {
        return slave.get(this);
    }

    public <R> IRemoteObject<R> callReturn(SerializableFunction<T, R> lambda) throws RemoteException {
        return slave.call(this, lambda);
    }

    public void call(SerializableConsumer<T> lambda) throws RemoteException {
        slave.call(this, lambda);
    }

    public IRemoteObject<T> move(Slave slave) throws RemoteException {
        this.slave.move(this, slave.getRemoteSlave());
        setSlave(slave.getRemoteSlave());
        return this;
    }

    public IRemoteObject<T> copy(Slave slave) throws RemoteException {
        return this.slave.copy(this, slave.getRemoteSlave());
    }
}
