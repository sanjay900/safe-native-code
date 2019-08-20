package server.backends;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.io.FilenameUtils;
import server.Retriever;
import shared.*;
import slave.Slave;
import slave.SlaveMain;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A ProcessBasedServer is used when we have a server that executes code in a slave process somewhere.
 */
abstract class ProcessBasedServer implements Server {
    private Slave slave;
    int registryPort;
    int slavePort;
    int lookupPort;
    private ClassLoader[] classLoaders;
    private boolean useAgent;

    /**
     * Create a slave that runs in another process somewhere
     *
     * @param useAgent     true to use a java agent to capture all classes, false to pass in classloaders below
     * @param classLoaders a list of classloads to supply classes to the slave, if useAgent is false
     * @throws IOException
     */
    ProcessBasedServer(boolean useAgent, ClassLoader... classLoaders) throws IOException {
        this.classLoaders = classLoaders;
        this.registryPort = findAvailablePort();
        this.slavePort = findAvailablePort();
        this.lookupPort = findAvailablePort();
        this.useAgent = useAgent;
        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));
    }

    private int findAvailablePort() throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.setReuseAddress(true);
        ss.bind(new InetSocketAddress(0));
        ss.close();
        return ss.getLocalPort();
    }

    String[] getJavaCommandArgs(String javaCommand, boolean jarWithPath, boolean isVagrant) {
        List<String> args = new ArrayList<>();
        args.add(javaCommand);
//        args.add("-Djava.system.class.loader=slave.SlaveClassloader");
        //Give us the ability to reflect into rmi so we can use it on VMs
        //On Java 9+, we need to explicitly grant ourselves access to the rmi module
        if (Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 9) {
            args.addAll(Arrays.asList("--add-opens", "java.rmi/sun.rmi.registry=ALL-UNNAMED"));
        }
        args.addAll(Arrays.asList("-cp", jarWithPath ? getJar().getAbsolutePath() : getJar().getName(), SlaveMain.class.getName()));
        args.addAll(Arrays.asList(registryPort + "", slavePort + "", lookupPort + ""));
        args.add(isVagrant + "");
        return args.toArray(new String[0]);
    }

    static File getJar() {
        try {
            File jar = new File(ProcessBasedServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (FilenameUtils.getExtension(jar.getPath()).equals("jar")) {
                return jar;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new File("build/libs/safeNativeCode.jar");
    }

    void setupRegistry() throws RemoteException, InterruptedException {
        Registry registry = LocateRegistry.getRegistry(registryPort);
        if (useAgent) {
            ByteBuddyAgent.attach(getJar(), ByteBuddyAgent.ProcessProvider.ForCurrentVm.INSTANCE, registryPort + " " + lookupPort);
            return;
        }

        while (true) {
            try {
                Retriever br = new Retriever(lookupPort, classLoaders);
                registry.rebind("bytecodeLookup", br);
                //Start a thread that monitors the remote process, and frees up the retrievers ports when it is completed.
                new Thread(() -> {
                    try {
                        this.waitForExit();
                        UnicastRemoteObject.unexportObject(br, true);
                    } catch (InterruptedException | IOException ignored) {

                    }
                }).start();
                break;
            } catch (RemoteException e) {
                Thread.sleep(10);
            }
        }
        while (true) {
            try {
                slave = (Slave) registry.lookup("slave");
                break;
            } catch (NotBoundException e) {
                Thread.sleep(10);
            }
        }
    }

    @Override
    public void call(SerializableRunnable lambda) throws RemoteException {
        slave.call(lambda);
    }

    @Override
    public <T> void call(RemoteObject<T> obj, SerializableConsumer<T> lambda) throws RemoteException {
        slave.call(obj, lambda);
    }

    @Override
    public <T> RemoteObject<T> copy(RemoteObject<T> original) throws RemoteException {
        return slave.copy(original);
    }

    public <T> RemoteObject<T> call(SerializableSupplier<T> lambda) throws RemoteException {
        return slave.call(lambda);
    }

    @Override
    public <R, T> RemoteObject<R> call(RemoteObject<T> t, One<R, T> lambda) throws RemoteException {
        return slave.call(t, lambda);
    }

    @Override
    public <R, T1, T2> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, Two<R, T1, T2> lambda) throws RemoteException {
        return slave.call(t1, t2, lambda);
    }

    @Override
    public <R, T1, T2, T3> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, Three<R, T1, T2, T3> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, Four<R, T1, T2, T3, T4> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, t4, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, Five<R, T1, T2, T3, T4, T5> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, t4, t5, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, Six<R, T1, T2, T3, T4, T5, T6> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, t4, t5, t6, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, Seven<R, T1, T2, T3, T4, T5, T6, T7> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, t4, t5, t6, t7, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, Eight<R, T1, T2, T3, T4, T5, T6, T7, T8> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, t4, t5, t6, t7, t8, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, Nine<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, t4, t5, t6, t7, t8, t9, lambda);
    }

    @Override
    public <R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RemoteObject<R> call(RemoteObject<T1> t1, RemoteObject<T2> t2, RemoteObject<T3> t3, RemoteObject<T4> t4, RemoteObject<T5> t5, RemoteObject<T6> t6, RemoteObject<T7> t7, RemoteObject<T8> t8, RemoteObject<T9> t9, RemoteObject<T10> t10, Ten<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) throws RemoteException {
        return slave.call(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, lambda);
    }

}