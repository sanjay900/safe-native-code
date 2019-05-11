import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class ClientJVM implements JVM {
    public Object object;
    private JVM stub;

    public ClientJVM(UUID uuid, Class clazz) throws RemoteException, IllegalAccessException, InstantiationException {
        Registry registry = LocateRegistry.getRegistry(1099);
        object = clazz.newInstance();
        stub = (JVM) UnicastRemoteObject.exportObject(this, 0);
        registry.rebind(uuid.toString(), stub);
    }

    public Object call(String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class[] c = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            c[i] = args[i].getClass();
        }
        return object.getClass().getDeclaredMethod(methodName, c).invoke(object, args);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, RemoteException, InstantiationException {
        new ClientJVM(UUID.fromString(args[0]), Class.forName(args[1]));
    }
}
