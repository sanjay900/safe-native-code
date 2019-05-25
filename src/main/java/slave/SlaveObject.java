package slave;

import server.RemoteObject;
import server.SerializableFunction;

import java.io.Serializable;

class SlaveObject<T> implements Serializable {
    private T object;
    private SlaveJVM remote;

    SlaveObject(Class<T> clazz, SlaveJVM remote) throws IllegalAccessException, InstantiationException {
        object = clazz.newInstance();
        this.remote = remote;
    }

    SlaveObject(T object, SlaveJVM remote) {
        this.object = object;
        this.remote = remote;
    }

    <R> RemoteObject<R> call(SerializableFunction<T, R> lambda) {
        return remote.wrap(lambda.apply(this.get()));
    }

    T get() {
        return object;
    }
}
