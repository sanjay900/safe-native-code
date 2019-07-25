package server;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SafeCodeLibrary {
    private static int rmiPort = -1;
    private static Registry registry;

    /**
     * Initlialise this library, using an agent to resolve any classes bytecode for dynamically passing it to a slave
     * @param rmiPort the port to connect over on rmi
     */
    public static void initialiseWithAgent(int rmiPort, File agentJar) throws RemoteException {
        initialise(rmiPort);
        ByteBuddyAgent.attach(agentJar, ByteBuddyAgent.ProcessProvider.ForCurrentVm.INSTANCE, rmiPort + "");
    }

    /**
     * Initlialise this library, using ClassLoaders to resolve class bytecode for dynamically passing it to a slave
     * Note that all classloaders must support getResourceAsStream so that we can pass all bytecode to the slave process
     * @param rmiPort the port to connect over on rmi
     */
    public static void initialiseWithClassLoaders(int rmiPort, ClassLoader... classLoaders) throws RemoteException {
        initialise(rmiPort);
        registry.rebind("bytecodeLookup", new BytecodeServer(classLoaders));
    }

    private static void initialise(int rmiPort) {
        if (rmiPort < 0) throw new RuntimeException("RMI Port should be > 0");
        try {
            registry = LocateRegistry.createRegistry(rmiPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        SafeCodeLibrary.rmiPort = rmiPort;
    }

    static Registry getRegistry() {
        return registry;
    }

    public static int getRMIPort() {
        return rmiPort;
    }
}
