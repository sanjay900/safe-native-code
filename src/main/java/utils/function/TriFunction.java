package utils.function;

import java.io.Serializable;

public interface TriFunction<R, T1, T2, T3> extends Serializable {
    R accept(T1 arg1, T2 arg2, T3 arg3);
}
