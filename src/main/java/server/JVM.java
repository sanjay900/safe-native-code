package server;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JVM extends Remote {
    RemoteObject newInst(Class<?> clazz) throws RemoteException, IllegalAccessException, InstantiationException;
    RemoteObject call(RemoteObject obj, String methodName, Object... args) throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    Object get(RemoteObject obj) throws RemoteException;
    RemoteObject move(RemoteObject obj, JVM destination) throws RemoteException;
    RemoteObject copy(Object object) throws RemoteException;
}
