package shared;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface SharedAPI {
    interface One<R, T1> extends Serializable {
        R accept(T1 args);
    }

    interface Two<R, T1, T2> extends Serializable {
        R accept(T1 arg1, T2 arg2);
    }

    interface Three<R, T1, T2, T3> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3);
    }

    interface Four<R, T1, T2, T3, T4> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4);
    }

    interface Five<R, T1, T2, T3, T4, T5> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5);
    }

    interface Six<R, T1, T2, T3, T4, T5, T6> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6);
    }

    interface Seven<R, T1, T2, T3, T4, T5, T6, T7> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7);
    }

    interface Eight<R, T1, T2, T3, T4, T5, T6, T7, T8> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8);
    }

    interface Nine<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8, T9 arg9);
    }

    interface Ten<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends Serializable {
        R accept(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8, T9 arg9, T10 arg10);
    }

    <T> RemoteObject<T> copy(RemoteObject<T> original) throws RemoteException;

    void call(SerializableRunnable lambda) throws RemoteException;
    <R> RemoteObject<R> call(SerializableSupplier<R> lambda) throws RemoteException;


    <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) throws RemoteException;

    <R, T> RemoteObject<R> call(RemoteObject<T> t, One<R, T> lambda) throws RemoteException;

    <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, Two<R, T1, T2> lambda) throws RemoteException;

    <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, Three<R, T1, T2, T3> lambda) throws RemoteException;

    <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, Four<R, T1, T2, T3, T4> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, Five<R, T1, T2, T3, T4, T5> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, Six<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, Seven<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, Eight<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, Nine<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException;

    <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, Ten<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException;
}
