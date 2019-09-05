package slave.types;

import slave.BytecodeSupplier;
import slave.RemoteObject;
import slave.Slave;
import slave.exceptions.SlaveDeadException;
import slave.exceptions.UnknownObjectException;
import utils.function.*;
import utils.function.Runnable;
import slave.process.SlaveProcessMain;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A AbstractSlave is used when we have a server that executes code in a slave process somewhere.
 */
public abstract class AbstractSlave implements SlaveType {
    private Slave slave;
    private int registryPort;
    private ClassLoader[] classLoaders;
    private boolean addShutdownHooks;

    /**
     * Create a slave that runs in another process somewhere
     *
     * @param useShutdownHook true if this server should register a hook to automatically handle cleanup
     * @param classLoaders    a list of classloaders to supply classes to the slave, if useAgent is false
     */
    AbstractSlave(boolean useShutdownHook, ClassLoader... classLoaders) throws IOException {
        if (classLoaders.length == 0) {
            throw new IOException("A classloader is expected!");
        }
        this.addShutdownHooks = useShutdownHook;
        this.classLoaders = classLoaders;
        this.registryPort = findAvailablePort();
        if (useShutdownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread(this::terminate));
        }
    }

    private int findAvailablePort() throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.setReuseAddress(true);
        ss.bind(new InetSocketAddress(0));
        ss.close();
        return ss.getLocalPort();
    }

    String[] getSlaveArgs() {
        return new String[]{registryPort + ""};
    }

    String[] getJavaCommandArgs(String javaCommand, String libLocation) {
        List<String> args = new ArrayList<>();
        args.add(javaCommand);
        args.add("-cp");
        args.add(Arrays.stream(getClassPath()).map(path -> libLocation + "/" + path).collect(Collectors.joining(":")));
        args.add(SlaveProcessMain.class.getName());
        args.addAll(Arrays.asList(getSlaveArgs()));
        return args.toArray(new String[0]);
    }

    static String[] getClassPath() {
        return Arrays
                .stream(System.getProperty("java.class.path").split(":"))
                .map(path -> Paths.get(path).toAbsolutePath().toString())
                .toArray(String[]::new);
    }

    void setupRegistry() throws RemoteException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry(registryPort);
        BytecodeSupplier retriever = new BytecodeSupplier(classLoaders);
        //Try repeatedly until the registry is active.
        while (true) {
            try {
                registry.rebind("bytecodeLookup", retriever);
                if (addShutdownHooks) {
                    //Start a thread that monitors the remote process, and frees up the retrievers ports when it is completed.
                    new Thread(() -> {
                        try {
                            this.waitForExit();
                            UnicastRemoteObject.unexportObject(retriever, true);
                        } catch (InterruptedException | IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }).start();
                }
                break;
            } catch (RemoteException e) {
                Thread.sleep(10);
            }
        }
        while (true) {
            try {
                slave = (Slave) registry.lookup("slave/process");
                break;
            } catch (NotBoundException e) {
                Thread.sleep(10);
            }
        }
    }

    private void checkAlive() {
        try {
            if (!isAlive()) {
                throw new SlaveDeadException(this);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(Runnable lambda) throws RemoteException {
        checkAlive();
        slave.run(lambda);
    }

    @Override
    public <T> void call(RemoteObject<T> obj, Consumer<T> lambda) throws RemoteException {
        checkAlive();
        slave.call(obj, lambda);
    }

    @Override
    public <T> RemoteObject<T> copy(RemoteObject<T> original) throws RemoteException {
        checkAlive();
        return slave.copy(original);
    }

    public <T> RemoteObject<T> call(Supplier<T> lambda) throws RemoteException {
        checkAlive();
        return slave.call(lambda);
    }

    @Override
    public <R, T> RemoteObject<R> call(RemoteObject<T> t, Function<R, T> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t, lambda);
    }

    @Override
    public <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, BiFunction<R, T1, T2> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, lambda);
    }

    @Override
    public <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, TriFunction<R, T1, T2, T3> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, QuadFunction<R, T1, T2, T3, T4> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, t4, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, FiveFunction<R, T1, T2, T3, T4, T5> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, t4, t5, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, SixFunction<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, t4, t5, t6, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, SevenFunction<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, t4, t5, t6, t7, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, EightFunction<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, t4, t5, t6, t7, t8, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, NineFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, t4, t5, t6, t7, t8, t9, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, TenFunction<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException {
        checkAlive();
        return slave.call(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, lambda);
    }
}