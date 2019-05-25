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

public class SlaveJVM implements ISlaveJVM {

    private transient Map<UUID, SlaveObject> clientObjects = new HashMap<>();

    private SlaveJVM(UUID uuid) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(1099);
        ISlaveJVM stub = (ISlaveJVM) UnicastRemoteObject.exportObject(this, 0);
        registry.rebind(uuid.toString(), stub);
    }

    @Override
    public <T> RemoteObject<T> newInst(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        RemoteObject<T> r = new RemoteObject<>(this);
        clientObjects.put(r.getUuid(), new SlaveObject<>(clazz, this));
        return r;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) {
        clientObjects.get(obj.getUuid()).call(lambda);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> RemoteObject<R> call(RemoteObject<T> obj, SerializableFunction<T, R> lambda) {
        return clientObjects.get(obj.getUuid()).call(lambda);
    }

    <T> RemoteObject<T> wrap(T object) {
        RemoteObject<T> r = new RemoteObject<>(this);
        clientObjects.put(r.getUuid(), new SlaveObject<>(object, this));
        return r;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(RemoteObject<T> obj) {
        return (T) clientObjects.get(obj.getUuid()).get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> RemoteObject<T> copy(RemoteObject<T> obj, ISlaveJVM destination) throws RemoteException {
        return destination.copy((T) clientObjects.get(obj.getUuid()).get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void move(RemoteObject<T> obj, ISlaveJVM destination) throws RemoteException {
        destination.move(obj, (T) clientObjects.remove(obj.getUuid()).get());
    }

    @Override
    public <T> RemoteObject<T> copy(T object) {
        return wrap(object);
    }

    @Override
    public <T> RemoteObject<T> move(RemoteObject<T> remoteObject, T object) {
        clientObjects.put(remoteObject.getUuid(), new SlaveObject<>(object, this));
        return remoteObject;
    }

    public static void main(String[] args) throws RemoteException {
        new SlaveJVM(UUID.fromString(args[0]));
    }
}
