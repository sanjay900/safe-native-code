package server.backends;

import shared.SlaveAPI;

import java.io.IOException;
import java.rmi.RemoteException;

public interface Backend extends SlaveAPI {
    void exit(int code) throws RemoteException;
    boolean isAlive() throws IOException, InterruptedException;
}
