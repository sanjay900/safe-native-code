package slaveProcess;

import shared.Retriever;
import shared.Utils;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.SecureClassLoader;
import java.util.Arrays;

/**
 * SlaveProcessClassloader facilitates loading classes from the main process, using RMI. We retrieve a Supplier from the host, and use it to load classes.
 */
public class SlaveProcessClassloader extends SecureClassLoader {
    private static Retriever lookup;
    //We need a list of prohibited classes, as we do not want to proxy core java classes through our proxy.
    private String[] prohibited = new String[]{"java.", "javax.", "com.sun.", "sun.", "jdk."};

    SlaveProcessClassloader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // We need to make sure the slave main and slave object are loaded using the correct classloader.
        // This is due to the fact that anything executed from the slave will use the parent's classloader.
        // And we need everything to go through this class loader, so that we can proxy calls to the main jvm when required.
        if (name.endsWith("slaveProcess.SlaveProcessClient") || name.endsWith("slaveProcess.SlaveProcessClient$1") || name.endsWith("slaveProcess.SlaveProcessClient$2") || name.endsWith("slaveProcess.SlaveProcessObject")) {
            // Essentially, we just reload these specific classes using this classloader instead of the app classloader.
            String className = name.replace(".", "/") + ".class";
            try {
                byte[] b = Utils.readStream(getResourceAsStream(className));
                return super.defineClass(name, b, 0, b.length);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        //This only occurs with DirectSlave, and in that case, we want server stuff loaded using the default classloader anyway.
        if (name.startsWith("shared") || name.startsWith("slave")) {
            return super.loadClass(name);
        }
        // If we don't have a reference to the main JVM yet, it's too early to need to proxy classes through it.
        // We also can not proxy core java classes, as java specifically blocks redefining anything inside the java package.
        if (lookup == null || Arrays.stream(prohibited).anyMatch(name::startsWith)) return super.loadClass(name);
        try {
            byte[] b = lookup.getByteCode(name);
            if (b == null) return super.loadClass(name);
            return super.defineClass(name, b, 0, b.length);
        } catch (RemoteException e) {
            // If we lose connection to the main JVM, just throw a class not found exception.
            throw new ClassNotFoundException("Unable to load: " + name, e);
        }
    }

    // Due to the fact that this is used across modules (SlaveProcessClassloader and SlaveProcessClient exist inside different ClassLoaders), we need to make it public.
    public static void setLookup(Retriever lookup) {
        SlaveProcessClassloader.lookup = lookup;
    }
}
