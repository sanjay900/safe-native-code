package utils.function;

import java.io.Serializable;

public interface Function<R, T1> extends Serializable {
    R apply(T1 t) throws Exception;
}
