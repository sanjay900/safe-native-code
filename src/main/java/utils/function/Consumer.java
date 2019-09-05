package utils.function;

import java.io.Serializable;

/**
 * A consumer that can be serialized, so we can call methods remotely
 */
public interface Consumer<T> extends Serializable {
    void accept(T t) throws Exception;
}
