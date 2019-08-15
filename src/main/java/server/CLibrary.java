package server;


import com.sun.jna.Native;

/**
 * CLibrary exposes prctl, so we can disable process dumping on unix based operating systems
 */
public class CLibrary {
    public static final int PR_SET_DUMPABLE = 4;

    static {
        Native.register("c");
    }

    public static native int prctl(int option, long arg2);
}
