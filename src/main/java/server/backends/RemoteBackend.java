package server.backends;

import server.SafeCodeLibrary;
import slave.ISlaveMain;
import slave.SlaveMain;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RemoteBackend extends SlaveBackend {
    public RemoteBackend(String... jvmOptions) throws IOException, InterruptedException, NotBoundException {
        Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
        UUID uuid = UUID.randomUUID();
        List<String> args = new ArrayList<>();
        args.add(javaProcess.toString());
        args.addAll(Arrays.asList(jvmOptions));
        args.add("-Djava.rmi.server.codebase=" + SafeCodeLibrary.getCodebase());
        args.addAll(Arrays.asList("-cp", System.getProperty("java.class.path"), SlaveMain.class.getName(), SafeCodeLibrary.getRMIPort() + "", uuid.toString()));
        Process process = new ProcessBuilder(args.toArray(new String[0])).inheritIO().start();
        //End the remoteSlave process if the parent process ends.
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        while (!Arrays.asList(SafeCodeLibrary.getRegistry().list()).contains(uuid.toString())) {
            Thread.sleep(10);
        }
        remoteSlave = (ISlaveMain) SafeCodeLibrary.getRegistry().lookup(uuid.toString());
    }
}
