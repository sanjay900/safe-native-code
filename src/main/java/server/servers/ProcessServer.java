package server.servers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A ProcessServer runs a slave in another process on the same machine that the host process is executed on.
 */
public class ProcessServer extends AbstractServer {
    private Process process;

    public ProcessServer(boolean useAgent, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(useAgent, true, classLoaders);
        Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
        process = new ProcessBuilder(getJavaCommandArgs(javaProcess.toString(), true, false)).inheritIO().start();
        //End the remoteSlave process if the parent process ends.
        Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
        setupRegistry();
    }

    @Override
    public void exit() {
        try {
            process.destroyForcibly().waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void waitForExit() throws InterruptedException {
        process.waitFor();
    }

    @Override
    public boolean isAlive() {
        return process.isAlive();
    }
}
