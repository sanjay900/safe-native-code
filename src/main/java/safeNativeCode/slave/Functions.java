package safeNativeCode.slave;

import java.io.Serializable;

/*
    Interfaces that allow for different types of arguments to be passed and be retrieved from a safeNativeCode.slave with lambdas
 */
public interface Functions {
    interface BiFunction<R, T1, T2> extends Serializable {
        R accept(T1 arg1, T2 arg2) throws Exception;
    }

    interface EightFunction<R, T1, T2, T3, T4, T5, T6, T7, T8> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8) throws Exception;
    }

    /**
     * A consumer that can be serialized, so we can call methods remotely
     */
    interface Consumer<T> extends Serializable {
        void accept(T t) throws Exception;
    }

    interface FiveFunction<R, T1, T2, T3, T4, T5> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) throws Exception;
    }

    interface Function<R, T1> extends Serializable {
        R apply(T1 t) throws Exception;
    }

    interface NineFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8, T9 arg9) throws Exception;
    }

    interface QuadFunction<R, T1, T2, T3, T4> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4) throws Exception;
    }

    interface Runnable extends Serializable {
        void run() throws Exception;
    }

    interface SevenFunction<R, T1, T2, T3, T4, T5, T6, T7> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7) throws Exception;
    }

    interface SixFunction<R, T1, T2, T3, T4, T5, T6> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6) throws Exception;
    }

    /**
     * A consumer that can be serialized, so we can call methods remotely
     */
    interface Supplier<T> extends Serializable {
        T get() throws Exception;
    }

    interface TenFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8, T9 arg9, T10 arg10) throws Exception;
    }

    interface TriFunction<R, T1, T2, T3> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3) throws Exception;
    }
}
