package slave;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A simple interface that allows for asking a remote process to do things
 * it can ask a remote process to hand over bytecode for a class
 */
public interface IBytecodeSupplier extends Remote {
    /**
     * Retrieve the bytecode for a class
     *
     * @param clazz the name of a class to retrieve bytecode for
     * @return the retrieved bytecode, or null if it does not exist
     */
    byte[] getByteCode(String clazz) throws RemoteException;
}
