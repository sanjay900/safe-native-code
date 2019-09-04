package shared;


import shared.Retriever;
import shared.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * An object that is responsible for replying to requests for information about classes, or for printing
 * data from remote processes.
 */
public class Supplier extends UnicastRemoteObject implements Retriever {
    private transient ClassLoader[] classLoaders;

    public Supplier(ClassLoader... classLoaders) throws RemoteException {
        super(0, null, port -> {
            ServerSocket ss = new ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(port));
            return ss;
        });
        this.classLoaders = classLoaders;
    }

    public byte[] getByteCode(String clazz) {
        for (ClassLoader loader : classLoaders) {
            InputStream is = loader.getResourceAsStream(clazz.replace(".", "/") + ".class");
            if (is != null) {
                try {
                    return Utils.readStream(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
