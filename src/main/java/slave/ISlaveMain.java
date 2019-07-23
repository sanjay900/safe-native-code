package slave;

import server.ISlave;

import java.rmi.RemoteException;

/**
 * ISlaveMain is the remote api used by RemoteObject for interfacing with a remote object.
 */
public interface ISlaveMain extends ISlave {

    <T> void move(RemoteObject<T> remoteObject, T object) throws RemoteException;

    <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) throws RemoteException;

    <T, R> RemoteObject<R> call(RemoteObject<T> obj, SerializableFunction<T, R> lambda) throws RemoteException;

    <T> T get(RemoteObject<T> obj) throws RemoteException, IncorrectSlaveException;

    <T> RemoteObject<T> copy(RemoteObject<T> obj, ISlaveMain destination) throws RemoteException;

    <T> void move(RemoteObject<T> obj, ISlaveMain destination) throws RemoteException;

    <T> RemoteObject<T> copy(T object) throws RemoteException;
}
