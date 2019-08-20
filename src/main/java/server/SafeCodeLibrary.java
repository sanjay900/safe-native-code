package server;

import org.apache.commons.lang.SystemUtils;
import preloader.loader.ClassPreloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static server.CLibrary.PR_SET_DUMPABLE;

public class SafeCodeLibrary {
    public static void secure() {
        if (SystemUtils.IS_OS_UNIX) {
            CLibrary.prctl(PR_SET_DUMPABLE, 0);
            try {
                int yamaVer = Integer.parseInt(Files.readAllLines(Paths.get("/proc/sys/kernel/yama/ptrace_scope")).get(0));
                if (yamaVer == 0) {
                    System.out.println("Your yama ptrace scope is set to 0. It needs to be set to 1 or higher in order to use this program.");
                    System.exit(1);
                }
            } catch (IOException e) {
                System.out.println("Your kernel version does not appear to support the yama security module. This is a requirement for this program.");
                System.out.println("Error: "+e.getLocalizedMessage());
                System.exit(1);
            }

        }
        new ClassPreloader().preload();
    }
}
