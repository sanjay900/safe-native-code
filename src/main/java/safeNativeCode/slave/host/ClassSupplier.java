package safeNativeCode.slave.host;


import safeNativeCode.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

/**
 * An object that is responsible for replying to requests for information about classes
 */
public class ClassSupplier extends UnicastRemoteObject implements IClassSupplier {
    private transient Set<ClassLoader> classLoaders;

    public ClassSupplier(Set<ClassLoader> classLoaders) throws RemoteException {
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
            InputStream is = loader.getResourceAsStream(clazz.replace(".", File.separator) + ".class");
            if (is != null) {
                try {
                    return Utils.readStream(is);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
