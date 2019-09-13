package slave.process;

import slave.IBytecodeSupplier;
import slave.Utils;

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
    //We need a list of prohibited classes, as we cannot recreate anything under the java package, or jdk.internal.reflect.SerializationConstructorAccessorImpl
    //Java automatically handles limiting access to creating anything in the java package, so we are free to match anything there.
    //We also have to avoid proxying slave.RemoteObject and slave.Slave, as directJVM will initialize them first with a different classloader.
    private String[] prohibited = new String[]{
            "java\\..*",
            "slave.RemoteObject",
            "slave.Slave",
            "slave.IBytecodeSupplier",
            "slave.process.ProcessClassLoader",
            "jdk.internal.reflect.SerializationConstructorAccessorImpl",
            "jdk.internal.reflect.MethodAccessorImpl",
            "slave.Functions(\\$.*)?",
    };
    //A list of classes that we need to load using a ProcessClassloader.
    //This is needed so that all any code run on the client, will use this classloader as its parent for loading classes.
    private List<String> forced = Arrays.asList(
            ProcessSlave.class.getName(),
            ProcessObject.class.getName()
    );

    ProcessClassloader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            if (forced.contains(name)) {
                // Reload these specific classes using this classloader instead of the app classloader.
                String className = name.replace(".", "/") + ".class";
                InputStream is = getResourceAsStream(className);
                if (is == null) {
                    throw new ClassNotFoundException("Unable to find: " + name);
                }
                byte[] b = Utils.readStream(is);
                return super.defineClass(name, b, 0, b.length);
            }
            if (bytecodeSupplier == null || Arrays.stream(prohibited).anyMatch(name::matches))
                return super.loadClass(name);
            System.out.println(name);
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
