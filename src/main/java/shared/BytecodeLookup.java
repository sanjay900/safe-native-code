package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A simple interface that allows for asking a remote process to hand over bytecode for a class
 */
public interface BytecodeLookup extends Remote {
    byte[] getByteCode(String clazz) throws RemoteException;
}
