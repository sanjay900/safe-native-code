package server.backends;

import org.apache.commons.io.FilenameUtils;
import server.BytecodeServer;
import server.RemoteObject;
import slave.ISlaveMain;
import slave.SerializableConsumer;
import slave.SerializableSupplier;
import slave.SlaveMain;

import java.io.File;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

abstract class ProcessBackend implements Backend {
    private ISlaveMain remoteSlave;
    protected int rmiPort = -1;
    private Registry registry;
    private UUID uuid = UUID.randomUUID();
    private ClassLoader[] classLoaders;

    public ProcessBackend(int rmiPort) {
        initPorts(rmiPort);
    }

    public ProcessBackend(int rmiPort, ClassLoader... classLoaders) {
        initPorts(rmiPort);
        this.classLoaders = classLoaders;
    }

    private void initPorts(int rmiPort) {
        this.rmiPort = rmiPort;
        for (int i = 0; i < 10; i++) {
            if (portsInUse.contains(rmiPort + i)) {
                throw new RuntimeException("Error, port " + rmiPort + " is in use by another backend.");
            }
            portsInUse.add(rmiPort + i);
        }
    }

    String[] getJavaCommandArgs(String javaCommand, boolean jarWithPath) {
        List<String> args = new ArrayList<>();
        args.add(javaCommand);
        args.add("-Djava.system.class.loader=slave.SlaveClassloader");
        //Give us the ability to reflect into rmi so we can use it on VMs and docker
        //On Java 9+, we need to explicitly grant ourselves access to the rmi module
        if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 9) {
            args.addAll(Arrays.asList("--add-opens", "java.rmi/sun.rmi.registry=ALL-UNNAMED"));
        }
        args.addAll(Arrays.asList("-cp", jarWithPath ? getJar().getAbsolutePath() : getJar().getName(), SlaveMain.class.getName(), rmiPort + "", uuid.toString()));
        return args.toArray(new String[0]);
    }

    public static File getJar() {
        try {
            File jar = new File(ProcessBackend.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (FilenameUtils.getExtension(jar.getPath()).equals("jar")) {
                return jar;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new File("build/libs/safeNativeCode.jar");
    }

    void initialise() throws RemoteException, InterruptedException {
        while (true) {
            try {
                registry = LocateRegistry.getRegistry(rmiPort);
                remoteSlave = (ISlaveMain) registry.lookup(uuid.toString());
                break;
            } catch (NotBoundException | RemoteException e) {
                Thread.sleep(10);
            }
        }
        if (classLoaders != null) {
            registry.rebind("bytecodeLookup", new BytecodeServer(rmiPort+1, classLoaders));
        }
    }

    @Override
    public <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) throws RemoteException {
        remoteSlave.call(obj, lambda);
    }

    @Override
    public <T> RemoteObject<T> copy(RemoteObject<T> original) throws RemoteException {
        return remoteSlave.copy(original);
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

}
