package server.servers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;

import java.io.IOException;
import java.net.SocketException;

/**
 * A Docker Server runs a slave inside a docker container.
 */
public class DockerServer extends AbstractServer {
    private DockerClient dockerClient;
    private CreateContainerResponse container;

    /**
     * Create a slave that runs inside a docker container
     *
     * @param classLoaders a list of classloaders to supply classes to the slave, if useAgent is false
     */
    @SuppressWarnings("deprecation")
    public DockerServer(ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(true, classLoaders);
        try {
            //Start a docker container based on our openjdk 12 image.
            DockerClientConfig config = DefaultDockerClientConfig.
                    createDefaultConfigBuilder()
                    .build();
            dockerClient = DockerClientBuilder.getInstance(config).build();
            PullImageResultCallback cb = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    super.onNext(item);
                    System.out.println(item.getStatus());
                }
            };
            dockerClient.pullImageCmd("openjdk").withTag("12").exec(cb);
            cb.awaitCompletion();
            //Share the folder containing the jar with the container.
            container = dockerClient.createContainerCmd("openjdk:12")
                    .withBinds(new Bind(getJar().toPath().toAbsolutePath().getParent().toString(), new Volume("/safeNativeCode")))
                    .withWorkingDir("/safeNativeCode")
                    .withCmd(getJavaCommandArgs("java", false))
                    .withNetworkMode("host")
                    .exec();
            dockerClient.startContainerCmd(container.getId()).exec();
            dockerClient.logContainerCmd(container.getId()).withStdOut(true).withStdErr(true).exec(new LogContainerResultCallback() {
                @Override
                public void onNext(Frame item) {
                    super.onNext(item);
                    if (item.getStreamType() == StreamType.STDOUT) {
                        System.out.println(new String(item.getPayload()));
                    }
                    if (item.getStreamType() == StreamType.STDERR) {
                        System.err.println(new String(item.getPayload()));
                    }
                }
            });
            setupRegistry();
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof SocketException) {
                throw new RuntimeException("Unable to connect to docker. is it running?");
            }
            throw ex;
        }
    }

    @Override
    public void terminate() {
        try {
            try {
                dockerClient.stopContainerCmd(container.getId()).exec();
            } catch (NotModifiedException ignored) {
                //If the container is already stopped, we get this exception.
            }
            dockerClient.removeContainerCmd(container.getId()).exec();
        } catch (NotFoundException ex) {
            //If the container has already been removed we end up here.
        }
    }

    @Override
    public void waitForExit() throws InterruptedException {
        WaitContainerResultCallback cb = new WaitContainerResultCallback();
        dockerClient.waitContainerCmd(container.getId()).exec(cb);
        cb.awaitCompletion();
    }

    @Override
    public boolean isAlive() {
        try {
            Boolean ret = dockerClient.inspectContainerCmd(container.getId()).exec().getState().getRunning();
            return ret != null && ret;
        } catch (NotFoundException ex) {
            //If the container isn't found, it has been killed and removed.
            return false;
        }
    }
}
