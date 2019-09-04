package shared;

import shared.exceptions.IncorrectSlaveException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Slave is the remote api used by RemoteObject for interfacing with a remote object.
 */
public interface Slave extends SharedAPI, Remote {

    <T> T get(RemoteObject<T> obj) throws RemoteException, IncorrectSlaveException;
}
