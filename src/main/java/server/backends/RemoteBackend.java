package server.backends;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoteBackend extends ProcessBackend {
    public RemoteBackend(int rmiPort) throws IOException, InterruptedException {
        super(rmiPort);
        startProcess();
    }

    public RemoteBackend(int rmiPort, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(rmiPort, classLoaders);
        startProcess();
    }

    private void startProcess() throws InterruptedException, IOException {
        Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
        Process process = new ProcessBuilder(getJavaCommandArgs(javaProcess.toString(), true, false)).inheritIO().start();
        //End the remoteSlave process if the parent process ends.
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        initialise();
    }
}
