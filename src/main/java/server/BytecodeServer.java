package server;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Start a web server hosting classes from a specific set of ClassLoaders or by using an Agent to host all classes
 */
public class BytecodeServer extends UnicastRemoteObject implements BytecodeLookup {
    private transient ClassLoader[] classLoaders;

    BytecodeServer(ClassLoader... classLoaders) throws RemoteException {
        this.classLoaders = classLoaders;
    }

    public byte[] getByteCode(String clazz) {
        if (BytecodeAgent.classFiles.containsKey(clazz)) {
            return BytecodeAgent.classFiles.get(clazz);
        }
        for (ClassLoader loader : classLoaders) {
            InputStream is = loader.getResourceAsStream(clazz);
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
}
