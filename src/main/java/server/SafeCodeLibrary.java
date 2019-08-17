package server;

import org.apache.commons.lang.SystemUtils;
import preloader.loader.ClassPreloader;

import static server.CLibrary.PR_SET_DUMPABLE;

public class SafeCodeLibrary {
    public static void secure() {
        if (SystemUtils.IS_OS_UNIX) {
            CLibrary.prctl(PR_SET_DUMPABLE, 0);
        }
        new ClassPreloader().preload();
    }
}
