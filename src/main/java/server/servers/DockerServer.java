package server.servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Docker Server runs a slave inside a docker container.
 */
public class DockerServer extends AbstractServer {
    private Process process;
    private String containerID;

    /**
     * Create a slave that runs inside a docker container, only sharing required folders for execution.
     *
     * @param classLoaders a list of classloaders to supply classes to the slave
     */
    public DockerServer(ClassLoader... classLoaders) throws IOException, InterruptedException {
        this(Collections.emptyList(), classLoaders);
    }

    /**
     * Create a slave that runs inside a docker container
     *
     * @param pathsToShare a list of directories to share with the container, the folder containing the jar is automatically shared.
     *                     Paths are mounted at /shared/path
     * @param classLoaders a list of classloaders to supply classes to the slave
     */
    public DockerServer(List<Path> pathsToShare, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(true, classLoaders);

        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList("docker", "create", "--rm", "--network", "host", "--workdir", "/safeNativeCode"));
        args.addAll(Arrays.asList("-v", getJar().toPath().toAbsolutePath().getParent().toString() + ":/safeNativeCode"));
        pathsToShare.forEach(path -> {
            String p = path.toAbsolutePath().toString();
            args.addAll(Arrays.asList("-v", p + ":/shared/" + p));
        });
        args.add("openjdk:12");
        args.addAll(Arrays.asList(getJavaCommandArgs("java", false)));
        Process process = new ProcessBuilder(args).start();
        process.waitFor();
        containerID = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        this.process = new ProcessBuilder("docker", "start", "-a", containerID).inheritIO().start();
        setupRegistry();
    }

    @Override
    public void terminate() {
        try {
            new ProcessBuilder("docker", "stop", containerID).start().waitFor();
            waitForExit();
        } catch (InterruptedException | IOException e) {
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
