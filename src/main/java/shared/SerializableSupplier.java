package shared;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * A consumer that can be serialized, so we can call methods remotely
 */
public interface SerializableSupplier<T> extends Supplier<T>, Serializable {
}
