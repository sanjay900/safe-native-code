package slave;

import java.io.Serializable;
import java.util.function.Function;

/**
 * A function that can be serialized so we can call functions remotely
 */
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
}
