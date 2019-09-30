package safeNativeCode.slave.host;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A ProcessSlave runs a safeNativeCode.slave in another process on the same machine that the host process is executed on.
 */
public class ProcessSlave extends AbstractSlave {
    private Process process;

    /**
     * Create a safeNativeCode.slave that runs inside another process
     *
     * @param classLoaders a list of classloaders to supply classes to the safeNativeCode.slave
     */
    public ProcessSlave(int timeLimit, String[] args, ClassLoader... classLoaders) {
        super(timeLimit, args, classLoaders);
        try {
            Path javaProcess = Paths.get(System.getProperty("java.home"), "bin", "java");
            process = new ProcessBuilder(getJavaCommandArgs(javaProcess.toString(), "")).inheritIO().start();
            //End the remoteSlave process if the parent process ends.
            Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
            setupRegistry();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void terminate() {
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
        return process != null && process.isAlive();
    }
}
