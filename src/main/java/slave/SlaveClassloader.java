package slave;

import shared.BytecodeLookup;

import java.rmi.RemoteException;
import java.security.SecureClassLoader;

/**
 * SlaveClassloader facilitates loading classes from the main process, using RMI. We retrieve a BytecodeLookup from the host, and use it to load classes.
 */
public class SlaveClassloader extends SecureClassLoader {
    private static BytecodeLookup lookup;

    public SlaveClassloader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (lookup == null) return super.findClass(name);
        byte[] b = new byte[0];
        try {
            b = lookup.getByteCode(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (b == null) return super.findClass(name);
        return super.defineClass(name, b, 0, b.length);
    }

    public static void setLookup(BytecodeLookup lookup) {
        SlaveClassloader.lookup = lookup;
    }
}
