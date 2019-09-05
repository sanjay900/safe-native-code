package slave.process;

import slave.IBytecodeSupplier;
import utils.Utils;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.SecureClassLoader;
import java.util.Arrays;

/**
 * SlaveProcessClassloader facilitates loading classes from the main process, using RMI. We retrieve a BytecodeSupplier from the host, and use it to load classes.
 */
public class SlaveProcessClassloader extends SecureClassLoader {
    private static IBytecodeSupplier bytecodeSupplier;
    //We need a list of prohibited classes, as we do not want to proxy core java classes through our proxy.
    //We also have to avoid proxying any of our classes that are used by the master, in the case of a direct JVM.
    private String[] prohibited = new String[]{"java.", "javax.", "com.sun.", "sun.", "jdk.", "library.", "slave.", "utils.function"};
    //A list of classes that we need to specifically define using this classloader.
    //This is needed so that all any code run on the client, will use this classloader as its parent for loading classes.
    private String[] wrap = new String[] {
            SlaveProcessClient.class.getName(),
            SlaveProcessClient.class.getName() + "$.*",
            SlaveProcessObject.class.getName()
    };

    SlaveProcessClassloader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (Arrays.stream(wrap).anyMatch(name::matches)) {
            // Reload these specific classes using this classloader instead of the app classloader.
            String className = name.replace(".", "/") + ".class";
            try {
                byte[] b = Utils.readStream(getResourceAsStream(className));
                return super.defineClass(name, b, 0, b.length);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        // If we don't have a reference to the main JVM yet, it's too early to need to proxy classes through it.
        // We also can not proxy core java classes, as java specifically blocks redefining anything inside the java package.
        if (bytecodeSupplier == null || Arrays.stream(prohibited).anyMatch(name::startsWith))
            return super.loadClass(name);
        try {
            byte[] b = bytecodeSupplier.getByteCode(name);
            if (b == null) return super.loadClass(name);
            return super.defineClass(name, b, 0, b.length);
        } catch (RemoteException e) {
            // If we lose connection to the main JVM, just throw a class not found exception.
            throw new ClassNotFoundException("Unable to load: " + name, e);
        }
    }

    // Due to the fact that this is used across modules (SlaveProcessClassloader and SlaveProcessClient exist inside different ClassLoaders), we need to make it public.
    public static void setByteCodeSupplier(IBytecodeSupplier bytecodeSupplier) {
        SlaveProcessClassloader.bytecodeSupplier = bytecodeSupplier;
    }
}
