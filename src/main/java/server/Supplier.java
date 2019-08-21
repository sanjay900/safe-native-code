package server;


import org.apache.commons.io.IOUtils;
import shared.Retriever;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
                    return IOUtils.toByteArray(is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    // It is entirely possible that a direct server may attempt to override System.out, so lets store a static instance here.
    private static PrintStream out = System.out;
    private static PrintStream err = System.err;

    public void printOut(int i) {
        out.write(i);
    }

    public void printErr(int i) {
        err.write(i);
    }
}
