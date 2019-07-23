package server;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteCodeManager {
    private static int codebasePort = -1;
    private static int rmiPort = -1;
    private static Registry registry;

    public static Class<?> getCallerClass() {
        try {
            StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
            for (int i = 1; i < stElements.length; i++) {
                StackTraceElement ste = stElements[i];
                if (!ste.getClassName().equals(RemoteCodeManager.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                    return Class.forName(ste.getClassName());
                }
            }
        } catch (ClassNotFoundException ignored) {

        }
        return null;
    }

    public static void InitialiseRemoteCode(int codebasePort, int rmiPort) {
        if (codebasePort < 0) throw new RuntimeException("Codebase Port should be > 0");
        if (rmiPort < 0) throw new RuntimeException("RMI Port should be > 0");
        try {
            registry = LocateRegistry.createRegistry(rmiPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        RemoteCodeManager.codebasePort = codebasePort;
        RemoteCodeManager.rmiPort = rmiPort;
        String args = "" + codebasePort;
        ByteBuddyAgent.attach(getRemoteJar(), ByteBuddyAgent.ProcessProvider.ForCurrentVm.INSTANCE, args);
        System.out.println(getCallerClass());
    }

    public static File getRemoteJar() {
        return new File("build/libs/safeNativeCode-1.0-SNAPSHOT.jar");
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
