package utils.function;

import java.io.Serializable;

/**
 * A consumer that can be serialized, so we can call methods remotely
 */
public interface Supplier<T> extends java.util.function.Supplier<T>, Serializable {
}
