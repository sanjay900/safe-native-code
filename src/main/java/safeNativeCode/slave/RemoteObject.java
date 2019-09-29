package safeNativeCode.slave;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * RemoteObject is used for wrapping an object that is stored in a remote process
 *
 * @param <T> the type of object this is wrapping
 */
public interface RemoteObject<T> extends Serializable {
    <R> RemoteObject<R> call(Functions.Function<R, T> lambda) throws RemoteException;

    /**
     * Execute a function on the safeNativeCode.slave, expecting no return values
     *
     * @param lambda the function to execute
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    void run(Functions.Consumer<T> lambda) throws RemoteException;

    /**
     * Copy this remote object to another safeNativeCode.slave
     *
     * @param slave the safeNativeCode.slave to move this object to
     * @return the new remote object on safeNativeCode.slave
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    RemoteObject<T> copyTo(Slave slave) throws RemoteException;

    /**
     * Get the value this remote object represents, serializing it in the process
     *
     * @return the value this remote object represents
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    T get() throws RemoteException;

    /**
     * Delete this object
     *
     * @throws RemoteException An error occurred while communicating with the remote JVM
     */
    void remove() throws RemoteException;

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
