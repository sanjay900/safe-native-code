package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JVM extends Remote {
    <T> RemoteObject<T> newInst(Class<T> clazz) throws RemoteException, IllegalAccessException, InstantiationException;

    <T, R> RemoteObject<R> call(RemoteObject<T> obj, SerializableFunction<T, R> lambda) throws RemoteException;

    <T> T get(RemoteObject<T> obj) throws RemoteException;

    <T> RemoteObject<T> move(RemoteObject<T> obj, JVM destination) throws RemoteException;

    <T> RemoteObject<T> copy(T object) throws RemoteException;
}
