package utils.function;

import java.io.Serializable;

/**
 * A consumer that can be serialized, so we can call methods remotely
 */
public interface Consumer<T> extends java.util.function.Consumer<T>, Serializable {
}
