package slave;

import server.SerializableConsumer;
import server.SerializableFunction;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SlaveMain is the Main class run by a slave
 */
public class SlaveMain implements ISlaveMain {

    private transient Map<RemoteObject, Object> localObjects = new HashMap<>();

    private SlaveMain(UUID uuid) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(1099);
        ISlaveMain stub = (ISlaveMain) UnicastRemoteObject.exportObject(this, 0);
        registry.rebind(uuid.toString(), stub);
    }

    @Override
    public <T> RemoteObject<T> newInst(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        RemoteObject<T> r = new RemoteObject<>(this);
        localObjects.put(r, clazz.newInstance());
        return r;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) {
        lambda.accept((T) localObjects.get(obj));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> RemoteObject<R> call(RemoteObject<T> obj, SerializableFunction<T, R> lambda) {
        return wrap(lambda.apply((T) localObjects.get(obj)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(RemoteObject<T> obj) {
        return (T) localObjects.get(obj);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> RemoteObject<T> copy(RemoteObject<T> obj, ISlaveMain destination) throws RemoteException {
        return destination.copy((T) localObjects.get(obj));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void move(RemoteObject<T> obj, ISlaveMain destination) throws RemoteException {
        destination.move(obj, (T) localObjects.remove(obj));
    }

    @Override
    public <T> RemoteObject<T> copy(T object) {
        return wrap(object);
    }

    @Override
    public <T> void move(RemoteObject<T> remoteObject, T object) {
        localObjects.put(remoteObject, object);
    }

    private <T> RemoteObject<T> wrap(T object) {
        RemoteObject<T> r = new RemoteObject<>(this);
        localObjects.put(r, object);
        return r;
    }

    public static void main(String[] args) throws RemoteException {
        new SlaveMain(UUID.fromString(args[0]));
    }
}
