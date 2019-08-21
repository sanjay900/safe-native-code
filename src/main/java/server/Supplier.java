package server;


import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.io.IOUtils;
import shared.Retriever;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * An object that is responsible for replying to requests for information about classes, or for printing
 * data from remote processes.
 */
public class Supplier extends UnicastRemoteObject implements Retriever {
    private transient ClassLoader[] classLoaders;
    private transient HashMap<String, byte[]> classFiles = new HashMap<>();

    public Supplier(int unicastPort, boolean useAgent, ClassLoader... classLoaders) throws RemoteException {
        super(unicastPort, null, port -> {
            ServerSocket ss = new ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(port));
            return ss;
        });
        if (useAgent) {
            Instrumentation instrumentation = ByteBuddyAgent.install();
            instrumentation.addTransformer(new BytecodeTransformer(classFiles), true);
            for (Class<?> c : instrumentation.getAllLoadedClasses()) {
                if (instrumentation.isModifiableClass(c)) {
                    try {
                        instrumentation.retransformClasses(c);
                    } catch (UnmodifiableClassException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        this.classLoaders = classLoaders;
    }

    public byte[] getByteCode(String clazz) {
        if (classFiles.containsKey(clazz)) {
            return classFiles.get(clazz);
        }
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
