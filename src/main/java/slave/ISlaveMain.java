package slave;

import server.RemoteObject;
import server.backends.Backend;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * ISlaveMain is the remote api used by RemoteObject for interfacing with a remote object.
 */
public interface ISlaveMain extends Backend, Remote {

    <T> T get(RemoteObject<T> obj) throws RemoteException, IncorrectSlaveException;
}
