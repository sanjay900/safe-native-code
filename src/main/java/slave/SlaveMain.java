package slave;

import server.SerializableConsumer;
import server.SerializableFunction;
import server.SerializableSupplier;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SlaveMain is the Main class run by a slave
 */
public class SlaveMain implements ISlaveMain {

    private transient Map<RemoteObject, Object> localObjects = new HashMap<>();

    private SlaveMain(UUID uuid) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(1099);
        ISlaveMain stub = (ISlaveMain) UnicastRemoteObject.exportObject(this, 0);
        registry.rebind(uuid.toString(), stub);
    }

    @Override
    public <T> RemoteObject<T> call(SerializableSupplier<T> lambda) {
        return wrap(lambda.get());
    }

    @Override
    public <R, T> RemoteObject<R> call(RemoteObject<T> t, One<R, T> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t)));
    }

    @Override
    public <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, Two<R, T1, T2> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2)));
    }

    @Override
    public <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, Three<R, T1, T2, T3> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3)));
    }

    @Override
    public <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, Four<R, T1, T2, T3, T4> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3), get(t4)));
    }

    @Override
    public <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, Five<R, T1, T2, T3, T4, T5> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3), get(t4), get(t5)));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, Six<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3), get(t4), get(t5), get(t6)));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, Seven<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3), get(t4), get(t5), get(t6), get(t7)));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, Eight<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3), get(t4), get(t5), get(t6), get(t7), get(t8)));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, Nine<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3), get(t4), get(t5), get(t6), get(t7), get(t8), get(t9)));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, Ten<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException {
        return wrap(lambda.accept(get(t1), get(t2), get(t3), get(t4), get(t5), get(t6), get(t7), get(t8), get(t9), get(t10)));
    }

    @Override
    public <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) {
        lambda.accept(get(obj));
    }

    @Override
    public <T, R> RemoteObject<R> call(RemoteObject<T> obj, SerializableFunction<T, R> lambda) {
        return wrap(lambda.apply(get(obj)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(RemoteObject<T> obj) throws IncorrectSlaveException {
        if (!localObjects.containsKey(obj)) {
            throw new IncorrectSlaveException();
        }
        return (T) localObjects.get(obj);
    }

    @Override
    public <T> RemoteObject<T> copy(RemoteObject<T> obj, ISlaveMain destination) throws RemoteException {
        return destination.copy(get(obj));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void move(RemoteObject<T> obj, ISlaveMain destination) throws RemoteException {
        if (!localObjects.containsKey(obj)) {
            throw new IncorrectSlaveException();
        }
        destination.move(obj, (T) localObjects.remove(obj));
    }

    @Override
    public <T> RemoteObject<T> copy(T object) {
        return wrap(object);
    }

    @Override
    public <T> void move(RemoteObject<T> remoteObject, T object) {
        localObjects.put(remoteObject, object);
    }

    private <T> RemoteObject<T> wrap(T object) {
        RemoteObject<T> r = new RemoteObject<>(this);
        localObjects.put(r, object);
        return r;
    }

    public static void main(String[] args) throws RemoteException {
        new SlaveMain(UUID.fromString(args[0]));
    }
}
