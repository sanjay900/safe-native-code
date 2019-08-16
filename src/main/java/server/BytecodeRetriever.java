package server;


import org.apache.commons.io.IOUtils;
import shared.BytecodeLookup;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * A class that retrieves bytecode from a specific set of ClassLoaders or BytecodeAgent
 */
public class BytecodeRetriever extends UnicastRemoteObject implements BytecodeLookup {
    private transient ClassLoader[] classLoaders;

    public BytecodeRetriever(int unicastPort, ClassLoader... classLoaders) throws RemoteException {
        super(unicastPort, null, port -> {
            ServerSocket ss = new ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(port));
            return ss;
        });
        this.classLoaders = classLoaders;
    }

    public byte[] getByteCode(String clazz) {
        if (BytecodeAgent.classFiles.containsKey(clazz)) {
            return BytecodeAgent.classFiles.get(clazz);
        }
        for (ClassLoader loader : classLoaders) {
            InputStream is = loader.getResourceAsStream(clazz + ".class");
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
