import server.backends.Server;
import shared.RemoteObject;
import shared.SerializableConsumer;

import java.rmi.RemoteException;

/**
 * A LocalSlaveObject is similar to a SlaveObject, except it is specifically designed for a local object running on the same jvm as the server
 * @param <T>
 */
public class LocalSlaveObject<T> implements RemoteObject<T> {
    private T obj;

    public LocalSlaveObject(T obj) {
        this.obj = obj;
    }

    @Override
    public <R> RemoteObject<R> call(Server.One<R, T> lambda) {
        return new LocalSlaveObject<>(lambda.accept(obj));
    }

    @Override
    public void run(SerializableConsumer<T> lambda) {
        lambda.accept(obj);
    }

    @Override
    public RemoteObject<T> copy(Server slave) throws RemoteException {
        return slave.copy(this);
    }

    @Override
    public T get() {
        return obj;
    }
}
