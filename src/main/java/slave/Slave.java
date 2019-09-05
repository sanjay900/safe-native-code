package slave;

import utils.function.Runnable;
import utils.function.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Slave is the remote api used by RemoteObject for interfacing with a remote object.
 */
public interface Slave extends Remote {

    <T> RemoteObject<T> copy(RemoteObject<T> original) throws RemoteException;

    void run(Runnable lambda) throws RemoteException;

    <R> RemoteObject<R> call(Supplier<R> lambda) throws RemoteException;

    <T> void call(RemoteObject<T> obj, Consumer<T> lambda) throws RemoteException;

    <R, T> RemoteObject<R> call(RemoteObject<T> t, Function<R, T> lambda) throws RemoteException;

    <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, BiFunction<R, T1, T2> lambda) throws RemoteException;

    <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, TriFunction<R, T1, T2, T3> lambda) throws RemoteException;

    <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, QuadFunction<R, T1, T2, T3, T4> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, FiveFunction<R, T1, T2, T3, T4, T5> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, SixFunction<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, SevenFunction<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, EightFunction<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, NineFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, TenFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException;

}
