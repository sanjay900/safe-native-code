package server;


import com.sun.jna.Native;

public class CLibrary {
    public static final int PR_SET_DUMPABLE = 4;

    static {
        Native.register("c");
    }

    public static native int prctl(int option, long arg2);
}
