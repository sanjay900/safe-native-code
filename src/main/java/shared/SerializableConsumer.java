package shared;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A consumer that can be serialized, so we can call methods remotely
 */
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {
}
