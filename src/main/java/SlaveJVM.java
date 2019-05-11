import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import java.util.stream.Stream;

public class SlaveJVM implements JVM {
    public UUID uuid = UUID.randomUUID();
    public Process jvm;
    public JVM client;

    public SlaveJVM(String classpath, String JVMOptions, Class main) throws IOException, NotBoundException {
        Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
        Registry registry = LocateRegistry.createRegistry(1099);
        jvm = new ProcessBuilder(javaProcess.toString(), JVMOptions, "-cp", classpath, ClientJVM.class.getName(), uuid.toString(), main.getName()).inheritIO().start();

        while (true) {
            try {
                if (Stream.of(registry.list()).anyMatch(s -> s.equals(uuid.toString()))) {
                    client = (JVM) registry.lookup(uuid.toString());
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public Object call(String methodName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, RemoteException {
        return client.call(methodName, args);
    }

    public static void main(String[] args) throws IOException, NotBoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        JVM slave = new SlaveJVM(System.getProperty("java.class.path"), "", Test.class);
        System.out.println(slave.call("calculateNumber", (Integer) 5, (Integer) 6));
    }
}
