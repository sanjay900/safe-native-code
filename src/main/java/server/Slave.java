package server;

import slave.ISlaveMain;
import slave.RemoteObject;
import slave.SerializableSupplier;
import slave.SlaveMain;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * This class is responsible for starting up the remote jvm and
 */
public class Slave implements ISlave {
    private ISlaveMain remoteSlave;

    public Slave(String... jvmOptions) throws IOException, InterruptedException, NotBoundException {
        Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
        UUID uuid = UUID.randomUUID();
        List<String> args = new ArrayList<>();
        args.add(javaProcess.toString());
        args.addAll(Arrays.asList(jvmOptions));
        args.add("-Djava.system.class.loader=slave.SlaveClassloader");
        args.addAll(Arrays.asList("-cp", System.getProperty("java.class.path"), SlaveMain.class.getName(), SafeCodeLibrary.getRMIPort() + "", uuid.toString()));
        Process process = new ProcessBuilder(args.toArray(new String[0])).inheritIO().start();
        //End the remoteSlave process if the parent process ends.
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        while (!Arrays.asList(SafeCodeLibrary.getRegistry().list()).contains(uuid.toString())) {
            Thread.sleep(10);
        }
        remoteSlave = (ISlaveMain) SafeCodeLibrary.getRegistry().lookup(uuid.toString());
    }

    public <T> RemoteObject<T> call(SerializableSupplier<T> lambda) throws RemoteException {
        return remoteSlave.call(lambda);
    }

    @Override
    public <R, T> RemoteObject<R> call(RemoteObject<T> t, One<R, T> lambda) throws RemoteException {
        return remoteSlave.call(t, lambda);
    }

    @Override
    public <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, Two<R, T1, T2> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, lambda);
    }

    @Override
    public <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, Three<R, T1, T2, T3> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, Four<R, T1, T2, T3, T4> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, t4, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, Five<R, T1, T2, T3, T4, T5> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, t4, t5, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, Six<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, t4, t5, t6, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, Seven<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, t4, t5, t6, t7, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, Eight<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, t4, t5, t6, t7, t8, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, Nine<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, t4, t5, t6, t7, t8, t9, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, Ten<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException {
        return remoteSlave.call(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, lambda);
    }

    public ISlaveMain getRemoteSlaveJVM() {
        return remoteSlave;
    }
}
