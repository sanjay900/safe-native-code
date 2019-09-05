package utils.function;

import java.io.Serializable;

public interface QuadFunction<R, T1, T2, T3, T4> extends Serializable {
    R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
}
