package utils.function;

import java.io.Serializable;

public interface BiFunction<R, T1, T2> extends Serializable {
    R accept(T1 arg1, T2 arg2);
}
