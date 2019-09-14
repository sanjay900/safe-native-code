package library;

import com.sun.jna.Native;
import preloader.ClassPreloader;
import slave.Utils;
import slave.process.ProcessMain;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


public class SafeCodeLibrary extends ClassLoader {
    private boolean secure = false;
    private String[] prohibited = new String[]{
            "java\\..*"
    };

    public SafeCodeLibrary(ClassLoader parent) {
        super(parent);
    }

    private static boolean isSecure() {
        try {
            Class<?> c = SafeCodeLibrary.class;
            Field f = c.getDeclaredField("secure");
            f.setAccessible(true);
            return f.getBoolean(ClassLoader.getSystemClassLoader());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CLibrary {
        static final int PR_SET_DUMPABLE = 4;

        static {
            Native.register("c");
        }

        public static native int prctl(int option, long arg2);
    }

    public static void secure() {
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
            System.out.println("You appear to be using windows. We cannot guarantee that windows is secure.");
        }
        new ClassPreloader().preload();
        if (!(ClassLoader.getSystemClassLoader() instanceof SafeCodeLibrary)) {
            System.out.println("You do not appear to be running this code using -Xshare:off -Djava.system.class.loader=library.SafeCodeLibrary.\n" +
                               "We cannot guarantee the safety of your program.");
            return;
        }
        //Use reflection to set secure, as we have multiple different classloaders at any time, and we have to gurantee the correct one is used.
        try {
            Class<?> c = ClassLoader.getSystemClassLoader().getParent().loadClass("library.SafeCodeLibrary");
            Field f = c.getDeclaredField("secure");
            f.setAccessible(true);
            f.setBoolean(ClassLoader.getSystemClassLoader(), true);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("LOADING: " + name);
        if (isSecure()) {
            throw new ClassNotFoundException("Unable to load class, class loading disabled");
        }
        try {
            if (Arrays.stream(prohibited).noneMatch(name::matches)) {
                // Reload these specific classes using this classloader instead of the app classloader.
                String className = name.replace(".", "/") + ".class";
                InputStream is = getResourceAsStream(className);
                if (is == null) {
                    throw new ClassNotFoundException("Unable to find: " + name);
                }
                byte[] b = Utils.readStream(is);
                return super.defineClass(name, b, 0, b.length);
            }
            return super.loadClass(name);
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


    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(ProcessMain.class.getClassLoader());
        SafeCodeLibrary.secure();
        System.out.println(SafeCodeLibrary.class.getClassLoader());
        Class.forName("NotRealClass");
    }
}
