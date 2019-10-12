package safeNativeCode.slave.host;

import safeNativeCode.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A Docker SlaveType runs a safeNativeCode.slave inside a docker container.
 */
public class DockerSlave extends AbstractSlave {
    private Process process;
    private String containerID;
    private static final String DOCKER_IMAGE = "openjdk:13";
    private List<Path> pathsToShare;

    public DockerSlave(ClassLoader... classLoaders) {
        this(0, new String[]{}, classLoaders);
    }

    /**
     * Create a safeNativeCode.slave that runs inside a docker container, only sharing required folders for execution.
     *
     * @param classLoaders a list of classloaders to supply classes to the safeNativeCode.slave
     */
    public DockerSlave(int timeLimit, String[] args, ClassLoader... classLoaders) {
        this(timeLimit, args, Collections.emptyList(), classLoaders);
    }

    /**
     * Create a safeNativeCode.slave that runs inside a docker container
     *
     * @param pathsToShare a list of directories to share with the container. All jars on the classpath are automatically shared.
     *                     Paths are mounted at /shared/path
     * @param classLoaders a list of classloaders to supply classes to the safeNativeCode.slave
     */
    public DockerSlave(int timeLimit, String[] jArgs, List<Path> pathsToShare, ClassLoader... classLoaders) {
        super(timeLimit, jArgs, classLoaders);
        this.pathsToShare = pathsToShare;
        start();

    }

    protected void start() {
        try {
            //Pull first so we can easily get status
            new ProcessBuilder("docker", "pull", DOCKER_IMAGE).inheritIO().start().waitFor();
            if (hasTimedOut()) return;
            List<String> args = new ArrayList<>(Arrays.asList("docker", "create", "--rm", "--network", "host"));
            if (Utils.isUnix()) {
                String username = System.getProperty("user.name");
                String uid = new String(Utils.readStream(Runtime.getRuntime().exec("id -u " + username).getInputStream())).trim();
                String gid = new String(Utils.readStream(Runtime.getRuntime().exec("id -g " + username).getInputStream())).trim();
                args.addAll(Arrays.asList("--user", uid + ":" + gid));
            } else {
                args.addAll(Arrays.asList("--user", System.getProperty("user.name")));
            }
            for (String cp : getClassPath()) {
                if (Files.exists(Paths.get(cp))) {
                    args.addAll(Arrays.asList("-v", cp + ":/safeNativeCode/" + cp + ":ro"));
                }
            }
            pathsToShare.forEach(path -> {
                if (Files.exists(path)) {
                    String p = path.toAbsolutePath().toString();
                    args.addAll(Arrays.asList("-v", p + ":/shared/" + p));
                } else {
                    System.out.println("Path: "+path+" does not exist, skipping mount!");
                }
            });
            args.add(DOCKER_IMAGE);
            args.addAll(Arrays.asList(getJavaCommandArgs("java", "/safeNativeCode/")));
            Process process = new ProcessBuilder(args).start();
            process.waitFor();
            containerID = new String(Utils.readStream(process.getInputStream())).trim();
            String error = new String(Utils.readStream(process.getErrorStream())).trim();
            if (!error.isEmpty()) {
                System.out.println("An error occurred while starting docker container:");
                System.out.println(error);
                return;
            }
            this.process = new ProcessBuilder("docker", "start", "-a", "-i", containerID).inheritIO().start();
            setupRegistry();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void terminate() {
        if (!isAlive()) return;
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
        return process != null && process.isAlive();
    }
}
