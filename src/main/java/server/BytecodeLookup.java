package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BytecodeLookup extends Remote {
    byte[] getByteCode(String clazz) throws RemoteException;
}
