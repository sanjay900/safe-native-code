package shared;

import preloader.ClassPreloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static shared.CLibrary.PR_SET_DUMPABLE;

public class SafeCodeLibrary {
    public static void secure() {
        //Mac is secure by default, and as a result does not have yama or prctl.
        if (isUnix() && !isMac()) {
            CLibrary.prctl(PR_SET_DUMPABLE, 0);
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

    private static boolean isSolaris() {
        return OS.contains("sunos");
    }
}