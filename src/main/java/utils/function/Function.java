package utils.function;

import java.io.Serializable;

public interface Function<R, T1> extends Serializable, java.util.function.Function<T1, R> { }
