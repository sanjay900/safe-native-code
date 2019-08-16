package server.backends;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoteBackend extends ProcessBackend {
    private Process process;
    public RemoteBackend(boolean useAgent, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(useAgent, classLoaders);
        startProcess();
    }

    private void startProcess() throws InterruptedException, IOException {
        Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
        process = new ProcessBuilder(getJavaCommandArgs(javaProcess.toString(), true, false)).start();
        //End the remoteSlave process if the parent process ends.
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        setupRegistry();
    }

    @Override
    public boolean isAlive() {
        return process.isAlive();
    }
}
