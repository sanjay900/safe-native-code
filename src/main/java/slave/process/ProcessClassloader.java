package slave.process;

import slave.*;
import slave.exceptions.SlaveException;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * ProcessClassloader facilitates loading classes from the main process, using RMI. We retrieve a BytecodeSupplier from the host, and use it to load classes.
 */
public class ProcessClassloader extends SecureClassLoader {
    private static IBytecodeSupplier bytecodeSupplier;
    //A list of classes that we need to load using a ProcessClassloader.
    //This is needed so that all any code run on the client, will use this classloader as its parent for loading classes.
    private List<String> forced = Arrays.asList(
            Functions.class.getName() + "($.*)?",
            Process.class.getName(),
            ProcessObject.class.getName(),
            ProcessSlave.class.getName(),
            SlaveInternal.class.getName(),
            RemoteObject.class.getName(),
            SlaveException.class.getName()
    );

    public ProcessClassloader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            String className = name.replace(".", "/") + ".class";
            for (String s : forced) {
                if (name.matches(s)) {
                    if (forced.stream().anyMatch(name::matches)) {
                        // Reload these specific classes using this classloader instead of the app classloader.
                        InputStream is = getResourceAsStream(className);
                        if (is == null) {
                            throw new ClassNotFoundException("Unable to find: " + name);
                        }
                        byte[] b = Utils.readStream(is);
                        return super.defineClass(name, b, 0, b.length);
                    }
                }
            }

            if (Utils.isJavaClass(name) || bytecodeSupplier == null) return super.loadClass(name);
            byte[] b = bytecodeSupplier.getByteCode(name);
            if (b == null) return super.loadClass(name);
            return super.defineClass(name, b, 0, b.length);
        } catch (IOException e) {
            // If we lose connection to the main JVM, just throw a class not found exception.
            throw new ClassNotFoundException("Unable to load: " + name, e);
        }
    }

    // Due to the fact that this is used across modules (ProcessClassloader and ProcessSlave exist inside different ClassLoaders), we need to make it public.
    public static void setByteCodeSupplier(IBytecodeSupplier bytecodeSupplier) {
        ProcessClassloader.bytecodeSupplier = bytecodeSupplier;
    }
}
