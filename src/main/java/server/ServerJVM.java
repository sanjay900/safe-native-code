package server;

import slave.SlaveJVM;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ServerJVM {
    private JVM client;
    private static Registry registry;

    static {
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public ServerJVM(String classpath, String... jvmOptions) throws IOException, InterruptedException, NotBoundException {
        Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
        UUID uuid = UUID.randomUUID();
        List<String> args = new ArrayList<>();
        args.add(javaProcess.toString());
        args.addAll(Arrays.asList(jvmOptions));
        args.addAll(Arrays.asList("-cp", classpath, SlaveJVM.class.getName(), uuid.toString()));
        Process process = new ProcessBuilder(args.toArray(new String[0])).inheritIO().start();
        //End the process
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        while (!Arrays.asList(registry.list()).contains(uuid.toString())) {
            Thread.sleep(100);
        }
        client = (JVM) registry.lookup(uuid.toString());
    }

    public <T>RemoteObject<T> newInst(Class<T> clazz) throws RemoteException, IllegalAccessException, InstantiationException {
        return client.newInst(clazz);
    }

    JVM getClient() {
        return client;
    }
}
