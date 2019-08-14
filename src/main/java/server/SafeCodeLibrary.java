package server;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SafeCodeLibrary {
    private static int codebasePort = -1;
    private static int rmiPort = -1;
    private static Registry registry;

    /**
     * Initlialise this library, using an agent to resolve any classes bytecode for dynamically passing it to a slave
     * @param codebasePort the port to serve java classes on
     * @param rmiPort the port to connect over on rmi
     */
    public static void initialiseWithAgent(int codebasePort, int rmiPort, File agentJar) {
        initialise(codebasePort, rmiPort);
        ByteBuddyAgent.attach(agentJar, ByteBuddyAgent.ProcessProvider.ForCurrentVm.INSTANCE, rmiPort + "");
    }

    /**
     * Initlialise this library, using ClassLoaders to resolve class bytecode for dynamically passing it to a slave
     * Note that all classloaders must support getResourceAsStream so that we can pass all bytecode to the slave process
     * @param codebasePort the port to serve java classes on
     * @param rmiPort the port to connect over on rmi
     */
    public static void initialiseWithClassLoaders(int codebasePort, int rmiPort, ClassLoader... classLoaders) {
        initialise(codebasePort, rmiPort);
        new BytecodeServer(codebasePort, classLoaders);
    }

    private static void initialise(int codebasePort, int rmiPort) {
        if (codebasePort < 0) throw new RuntimeException("Codebase Port should be > 0");
        if (rmiPort < 0) throw new RuntimeException("RMI Port should be > 0");
        try {
            registry = LocateRegistry.createRegistry(rmiPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        SafeCodeLibrary.codebasePort = codebasePort;
        SafeCodeLibrary.rmiPort = rmiPort;
    }

    public static String getCodebase() {
        if (codebasePort < 0) throw new RuntimeException("Remote Code Manager not initialized!");
        return "http://localhost:" + codebasePort + "/";
    }

    public static Registry getRegistry() {
        return registry;
    }

    public static int getRMIPort() {
        return rmiPort;
    }
}
