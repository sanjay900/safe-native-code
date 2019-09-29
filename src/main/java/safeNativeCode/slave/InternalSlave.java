package safeNativeCode.slave;

import safeNativeCode.exceptions.UnknownObjectException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Slave is the remote api used by RemoteObject for interfacing with a remote object.
 */
public interface InternalSlave extends Remote {

    <T> RemoteObject<T> copy(RemoteObject<T> original) throws RemoteException;

    void run(Functions.Runnable lambda) throws RemoteException;

    <R> RemoteObject<R> call(Functions.Supplier<R> lambda) throws RemoteException;

    <T> void call(RemoteObject<T> obj, Functions.Consumer<T> lambda) throws RemoteException;

    <R, T> RemoteObject<R> call(RemoteObject<T> t, Functions.Function<R, T> lambda) throws RemoteException;

    <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, Functions.BiFunction<R, T1, T2> lambda) throws RemoteException;

    <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, Functions.TriFunction<R, T1, T2, T3> lambda) throws RemoteException;

    <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, Functions.QuadFunction<R, T1, T2, T3, T4> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, Functions.FiveFunction<R, T1, T2, T3, T4, T5> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, Functions.SixFunction<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, Functions.SevenFunction<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, Functions.EightFunction<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, Functions.NineFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, Functions.TenFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException;

    <T> T get(RemoteObject<T> obj) throws RemoteException, UnknownObjectException;

    void remove(RemoteObject obj) throws RemoteException, UnknownObjectException;
}
