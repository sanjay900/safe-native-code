package server.backends;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.IOException;

/**
 * A Docker Server runs a slave inside a docker container.
 */
public class DockerServer extends ProcessBasedServer {
    private DockerClient dockerClient;
    private CreateContainerResponse container;

    @SuppressWarnings("deprecation")
    public DockerServer(boolean useAgent, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(useAgent, classLoaders);
        DockerClientConfig config = DefaultDockerClientConfig.
                createDefaultConfigBuilder()
                .build();
        dockerClient = DockerClientBuilder.getInstance(config).build();
        PullImageResultCallback cb = new PullImageResultCallback();
        dockerClient.pullImageCmd("openjdk").withTag("12").exec(cb);
        cb.awaitCompletion();
        ExposedPort exposedRMI = ExposedPort.tcp(lookupPort);
        Ports portBindings = new Ports();
        portBindings.bind(exposedRMI, Ports.Binding.bindPort(lookupPort));
        container = dockerClient.createContainerCmd("openjdk:12")
                .withBinds(new Bind(getJar().toPath().toAbsolutePath().getParent().toString(), new Volume("/safeNativeCode")))
                .withWorkingDir("/safeNativeCode")
                .withCmd(getJavaCommandArgs("java", false, false))
                .withNetworkMode("host")
                .exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        setupRegistry();
    }

    @Override
    public void exit() {
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
