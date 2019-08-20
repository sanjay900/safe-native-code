package server;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.ProtectionDomain;
import java.util.HashMap;

/**
 * A small java agent that collects up classes that are loaded so they can be shared with another process.
 */
public class BytecodeAgent implements ClassFileTransformer {
    /**
     * This HashMap stores all class files that are encountered
     */
    static HashMap<String, byte[]> classFiles = new HashMap<>();

    public static void agentmain(String args, Instrumentation instrumentation) throws RemoteException, InterruptedException, UnmodifiableClassException {
        int registryPort = Integer.parseInt(args.split(" ")[0]);
        int lookupPort = Integer.parseInt(args.split(" ")[1]);
        Supplier supplier = new Supplier(lookupPort, ClassLoader.getSystemClassLoader());
        //Add a transformer that simply stores all classes encountered to classFiles
        instrumentation.addTransformer(new BytecodeAgent(), true);
        for (Class<?> c : instrumentation.getAllLoadedClasses()) {
            if (instrumentation.isModifiableClass(c)) {
                instrumentation.retransformClasses(c);
            }
        }

        Registry registry = LocateRegistry.getRegistry(registryPort);
        while (true) {
            try {
                registry.rebind("bytecodeLookup", supplier);
                break;
            } catch (RemoteException ex) {
                ex.printStackTrace();
                Thread.sleep(10);
            }
        }
        new Thread(() -> {
            while (true) {
                try {
                    registry.list();
                    Thread.sleep(100);
                } catch (RemoteException | InterruptedException ignored) {
                    //time to assume that the process has died and that is why we have lost access to the registry
                    try {
                        UnicastRemoteObject.unexportObject(supplier, true);
                    } catch (NoSuchObjectException ignored2) {
                        //If the object isn't shared, we don't care, chances are the main process has tidied things up already.
                    }
                }
            }
        }).start();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        //store this class file's content in classFiles, and structure its name so it is easily retrieved by rmi
        classFiles.put(className, classfileBuffer);
        return classfileBuffer;
    }
}
