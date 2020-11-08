package safeNativeCode.slave.process;

import safeNativeCode.exceptions.SlaveException;
import safeNativeCode.slave.Functions;
import safeNativeCode.slave.InternalSlave;
import safeNativeCode.slave.RemoteObject;
import safeNativeCode.slave.host.IClassSupplier;
import safeNativeCode.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * ProcessClassloader facilitates loading classes from the main process, using RMI. We retrieve a BytecodeSupplier from the host, and use it to load classes.
 */
public class ProcessClassloader extends SecureClassLoader {
    private static IClassSupplier bytecodeSupplier;

    public ProcessClassloader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (bytecodeSupplier != null) {
            try {
                byte[] b = bytecodeSupplier.getByteCode(name);
                if (b == null || Utils.isJavaClass(name) || name.startsWith("safeNativeCode.")) return super.loadClass(name);
                return super.defineClass(name, b, 0, b.length);
            } catch (IOException e) {
                // If we lose connection to the main JVM, just throw a class not found exception.
                throw new ClassNotFoundException("Unable to load: " + name, e);
            }
        }
        return super.loadClass(name);
    }

    // Due to the fact that this is used across modules (ProcessClassloader and ProcessSlave exist inside different ClassLoaders), we need to make it public.
    public static void setByteCodeSupplier(IClassSupplier bytecodeSupplier) {
        ProcessClassloader.bytecodeSupplier = bytecodeSupplier;
    }
}