package slave;

import server.ISlave;
import server.SerializableConsumer;
import server.SerializableFunction;

import java.rmi.RemoteException;

/**
 * ISlaveJVM is an api used by slaves for interfacing with remote objects
 */
public interface ISlaveJVM extends ISlave {

    <T> RemoteObject<T> move(RemoteObject<T> remoteObject, T object) throws RemoteException;


    /**
     * Execute a runnable on the slave, passing in a remote object
     *
     * @param obj    the object to pass into the function
     * @param lambda the function to execute
     * @throws RemoteException Exception occurred while communicating with slave
     */
    <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) throws RemoteException;

    /**
     * Execute a function on the slave, passing in a remote object
     *
     * @param obj    the object to pass into the function
     * @param lambda the function to execute
     * @param <R>    the return type of the function
     * @return A wrapped copy of the result of the function
     * @throws RemoteException Exception occurred while communicating with slave
     */
    <T, R> RemoteObject<R> call(RemoteObject<T> obj, SerializableFunction<T, R> lambda) throws RemoteException;

    /**
     * Get the real value from a wrapped one.
     *
     * @param obj the object to retrieve a value from
     * @return the real value
     * @throws RemoteException Exception occurred while communicating with slave
     */
    <T> T get(RemoteObject<T> obj) throws RemoteException;

    /**
     * Copy an object to another slave
     *
     * @param obj         the object to copy
     * @param destination the slave to copy it to
     * @return the remote object on the new slave
     * @throws RemoteException Exception occurred while communicating with slave
     */
    <T> RemoteObject<T> copy(RemoteObject<T> obj, ISlaveJVM destination) throws RemoteException;


    /**
     * Move an object to another slave
     *
     * @param obj         the object to copy
     * @param destination the slave to copy it to
     * @throws RemoteException Exception occurred while communicating with slave
     */
    <T> void move(RemoteObject<T> obj, ISlaveJVM destination) throws RemoteException;

    /**
     * Copy an object to a slave
     *
     * @param object the object to copy
     * @return the remote object wrapping the copied object
     * @throws RemoteException Exception occurred while communicating with slave
     */
    <T> RemoteObject<T> copy(T object) throws RemoteException;
}
