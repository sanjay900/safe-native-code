package server.backends;

import com.rits.cloning.Cloner;
import server.LocalSlaveObject;
import shared.RemoteObject;
import shared.SerializableConsumer;
import shared.SerializableRunnable;
import shared.SerializableSupplier;

import java.rmi.RemoteException;

/**
 * A Direct Server is a server that does absolutely nothing and just runs the code on the same JVM.
 * This is obviously insecure, but it allows for having something to benchmark against.
 */
public class DirectServer implements Server {

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public void waitForExit() {
        //One would hope the direct server never actually exits..... So we just do nothing.
    }

    @Override
    public <T> RemoteObject<T> copy(RemoteObject<T> original) throws RemoteException {
        return new LocalSlaveObject<>(Cloner.shared().deepClone(original.get()));
    }

    @Override
    public void call(SerializableRunnable lambda) throws RemoteException {
        lambda.run();
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public <R> RemoteObject<R> call(SerializableSupplier<R> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.get());
    }

    @Override
    public <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) throws RemoteException {
        lambda.accept(obj.get());
    }

    @Override
    public <R, T> RemoteObject<R> call(RemoteObject<T> t, One<R, T> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t.get()));
    }

    @Override
    public <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, Two<R, T1, T2> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get()));
    }

    @Override
    public <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, Three<R, T1, T2, T3> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get()));
    }

    @Override
    public <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, Four<R, T1, T2, T3, T4> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get(), t4.get()));
    }

    @Override
    public <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, Five<R, T1, T2, T3, T4, T5> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get(), t4.get(), t5.get()));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, Six<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get(), t4.get(), t5.get(), t6.get()));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, Seven<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get(), t4.get(), t5.get(), t6.get(), t7.get()));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, Eight<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get(), t4.get(), t5.get(), t6.get(), t7.get(), t8.get()));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, Nine<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get(), t4.get(), t5.get(), t6.get(), t7.get(), t8.get(), t9.get()));
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, Ten<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException {
        return new LocalSlaveObject<>(lambda.accept(t1.get(), t2.get(), t3.get(), t4.get(), t5.get(), t6.get(), t7.get(), t8.get(), t9.get(), t10.get()));
    }
}
