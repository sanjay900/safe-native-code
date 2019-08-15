package slave;

import server.BytecodeLookup;
import server.RemoteObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * SlaveMain is the Main class run by a slave
 */
public class SlaveMain extends UnicastRemoteObject implements ISlaveMain {

    private transient Map<SlaveRemoteObject, Object> localObjects = new HashMap<>();

    @SuppressWarnings("unchecked")
    private SlaveMain(int port, UUID uuid) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException, NotBoundException {
        super(port + 2);


        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(uuid.toString(), this);
        if (new File(".").getAbsolutePath().contains("vagrant")) {
            //For vagrant, requests don't come from localhost, and instead come from 10.0.2.2.
            //For vagrant, we have to proxy back the requests for bytecodeLookup back to the host, as the host isn't localhost.
            Field f = Class.forName("sun.rmi.registry.RegistryImpl").getDeclaredField("allowedAccessCache");
            f.setAccessible(true);
            InetAddress host = InetAddress.getByName("10.0.2.2");
            ((Hashtable)f.get(null)).put(host, host);
            new ProcessBuilder("socat", "tcp-l:" + (port + 1) + ",fork,reuseaddr", "tcp:10.0.2.2:" + (port + 1)).inheritIO().start();
        }

        while (!Arrays.asList(registry.list()).contains("bytecodeLookup")) {
            Thread.sleep(10);
        }
        SlaveClassloader.lookup = (BytecodeLookup) registry.lookup("bytecodeLookup");
        System.out.println("Slave Started.");
    }

    @Override
    public <T> RemoteObject<T> call(SerializableSupplier<T> lambda) {
        return wrap(lambda.get());
    }

    @Override
    public <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) throws RemoteException {
        lambda.accept(get(obj));
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

    @SuppressWarnings("unchecked")
    public <T> T get(RemoteObject<T> obj) throws IncorrectSlaveException {
        if (!(obj instanceof SlaveRemoteObject) || !localObjects.containsKey(obj)) {
            throw new IncorrectSlaveException();
        }
        return (T) localObjects.get(obj);
    }

    @Override
    public <T> RemoteObject<T> copy(RemoteObject<T> object) throws RemoteException {
        return wrap(object.get());
    }

    private <T> RemoteObject<T> wrap(T object) {
        SlaveRemoteObject<T> r = new SlaveRemoteObject<>(this);
        localObjects.put(r, object);
        return r;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, NotBoundException, NoSuchFieldException, IllegalAccessException {
        new SlaveMain(Integer.parseInt(args[args.length - 2]), UUID.fromString(args[args.length - 1]));
    }
}
