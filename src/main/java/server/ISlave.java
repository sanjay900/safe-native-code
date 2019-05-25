package server;

import slave.RemoteObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A ISlave is just an api for controlling a remote JVM instance from the server
 */
public interface ISlave extends Remote {

    /**
     * Create an object on a slave, and return a wrapper for interacting with it
     *
     * @param clazz the class to instantiate
     * @return A wrapped copy of the instantiated object
     * @throws RemoteException        Exception occurred while communicating with slave
     * @throws IllegalAccessException IllegalAccessException while trying to instantiate
     * @throws InstantiationException InstantiationException while trying to instantiate
     */
    <T> RemoteObject<T> newInst(Class<T> clazz) throws RemoteException, IllegalAccessException, InstantiationException;
}
