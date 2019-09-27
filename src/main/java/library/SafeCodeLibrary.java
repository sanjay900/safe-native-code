package library;

import com.sun.jna.Native;
import preloader.ClassPreloader;
import slave.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


public class SafeCodeLibrary extends ClassLoader {
    private boolean secure;
    private boolean securing = false;

    public SafeCodeLibrary(ClassLoader parent) {
        super(parent);
    }

    private void preload() {
        //Mac is secure by default, and as a result does not have yama or prctl.
        if (isUnix() && !isMac()) {
            CLibrary.prctl(CLibrary.PR_SET_DUMPABLE, 0);
            try {
                int yamaVer = Integer.parseInt(Files.readAllLines(Paths.get("/proc/sys/kernel/yama/ptrace_scope")).get(0));
                if (yamaVer == 0) {
                    System.err.println("Your yama ptrace scope is set to 0. It needs to be set to 1 or higher in order to use this program.");
                    System.exit(1);
                }
            } catch (IOException e) {
                System.err.println("Your kernel version does not appear to support the yama security module. This is a requirement for this program.");
                System.err.println("Error: " + e.getLocalizedMessage());
                System.exit(1);
            }
        }
        if (isWindows()) {
            System.err.println("You appear to be using windows. We cannot guarantee the security of windows when using this program.");
        }
        new ClassPreloader().preload(this, loaded);
        secure = true;
    }

    private static class CLibrary {
        static final int PR_SET_DUMPABLE = 4;

        static {
            Native.register("c");
        }

        public static native int prctl(int option, long arg2);
    }

    private Set<String> loaded = new HashSet<>();
    private ClassNotFoundException disabled;

    //We can't just throw a ClassLoadingDisabledException, since we need the exception to be loaded using this classloader. Load it again.
    private ClassNotFoundException getDisabledException() {
        if (disabled == null) {
            try {
                disabled = (ClassNotFoundException) loadClass(ClassLoadingDisabledException.class.getName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return disabled;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (!securing) {
            loaded.add(name);
            securing = true;
            preload();
        }
        Class<?> c = findLoadedClass(name);
        if (c != null) {
            return c;
        }
        //Skip specific junit classes that don't correctly handle reloading, or java classes.
        //Junit classes are only ignored when -Dtesting=true is passed as an argument however,
        if (Utils.isTestingClass(name) || Utils.isJavaClass(name)) {
            return super.loadClass(name);
        }
        if (secure && !loaded.contains(name)) {
            throw getDisabledException();
        }
        try {
            String className = name.replace(".", "/") + ".class";
            InputStream is = getResourceAsStream(className);
            if (is == null) {
                throw new ClassNotFoundException("Unable to find: " + name);
            }
            byte[] b = Utils.readStream(is);
            return super.defineClass(name, b, 0, b.length);
        } catch (IOException e) {
            // If we lose connection to the main JVM, just throw a class not found exception.
            throw new ClassNotFoundException("Unable to load: " + name, e);
        }
    }

    private static String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return OS.contains("win");
    }

    private static boolean isMac() {
        return OS.contains("mac");
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
}
