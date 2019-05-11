import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JVM extends Remote {
    Object call(String methodName, Object... args) throws RemoteException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
