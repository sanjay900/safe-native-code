package server;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * API for communicating with a remote object
 */
public interface IRemoteObject<T> extends Serializable {

    <R> IRemoteObject<R> callReturn(SerializableFunction<T, R> lambda) throws RemoteException;

    void call(SerializableConsumer<T> lambda) throws RemoteException;

    T get() throws RemoteException;

    IRemoteObject<T> move(Slave slave) throws RemoteException;

    IRemoteObject<T> copy(Slave slave) throws RemoteException;
}
