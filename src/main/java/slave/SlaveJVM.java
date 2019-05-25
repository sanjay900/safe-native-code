package slave;

import server.JVM;
import server.RemoteObject;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlaveJVM implements JVM {

    private transient Map<UUID, SlaveObject> clientObjects = new HashMap<>();

    private SlaveJVM(UUID uuid) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(1099);
        JVM stub = (JVM) UnicastRemoteObject.exportObject(this, 0);
        registry.rebind(uuid.toString(), stub);
    }

    @Override
    public RemoteObject newInst(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        RemoteObject r = new RemoteObject(this);
        clientObjects.put(r.getUuid(), new SlaveObject(clazz, this));
        return r;
    }

    RemoteObject wrap(Object object) {
        RemoteObject r = new RemoteObject(this);
        clientObjects.put(r.getUuid(), new SlaveObject(object, this));
        return r;
    }

    @Override
    public RemoteObject call(RemoteObject obj, String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return clientObjects.get(obj.getUuid()).call(methodName, args);
    }

    @Override
    public Object get(RemoteObject obj) {
        return clientObjects.get(obj.getUuid()).get();
    }

    @Override
    public RemoteObject move(RemoteObject obj, JVM destination) throws RemoteException {
        return destination.copy(clientObjects.remove(obj.getUuid()).get());
    }

    @Override
    public RemoteObject copy(Object object) {
        return wrap(object);
    }

    public static void main(String[] args) throws RemoteException {
        new SlaveJVM(UUID.fromString(args[0]));
    }
}
