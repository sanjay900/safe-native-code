package server;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.ProtectionDomain;
import java.util.HashMap;

public class BytecodeAgent implements ClassFileTransformer {
    /**
     * This HashMap stores all class files that are encountered
     */
    static HashMap<String, byte[]> classFiles = new HashMap<>();
    public static void agentmain(String args, Instrumentation instrumentation) throws RemoteException {
        int port = Integer.parseInt(args);
        BytecodeServer server = new BytecodeServer(port+2, ClassLoader.getSystemClassLoader());
        //Add a transformer that simply stores all classes encountered to classFiles
        instrumentation.addTransformer(new BytecodeAgent());
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind("bytecodeLookup", server);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        //store this class file's content in classFiles, and structure its name so it is easily retrieved by rmi
        classFiles.put(className + ".class", classfileBuffer);
        return classfileBuffer;
    }
}
