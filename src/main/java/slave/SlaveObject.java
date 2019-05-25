package slave;

import org.apache.commons.lang3.ClassUtils;
import server.RemoteObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

class SlaveObject implements Serializable {
    private Object object;
    private SlaveJVM remote;

    SlaveObject(Class<?> clazz, SlaveJVM remote) throws IllegalAccessException, InstantiationException {
        object = clazz.newInstance();
        this.remote = remote;
    }

    SlaveObject(Object object, SlaveJVM remote) {
        this.object = object;
        this.remote = remote;
    }

    RemoteObject call(String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class[] c = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            c[i] = args[i].getClass();
        }
        return remote.wrap(object.getClass().getDeclaredMethod(methodName, ClassUtils.wrappersToPrimitives(c)).invoke(object, args));
    }
    Object get() {
        return object;
    }
}
