package server.backends;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.IOException;
import java.rmi.NotBoundException;

public class DockerBackend extends ProcessBackend {
    public DockerBackend(int rmiPort) throws IOException, NotBoundException, InterruptedException {
        super(rmiPort);
        startProcess();
    }

    public DockerBackend(int rmiPort, ClassLoader... classLoaders) throws IOException, NotBoundException, InterruptedException {
        super(rmiPort, classLoaders);
        startProcess();
    }

    private void startProcess() throws InterruptedException, IOException, NotBoundException {
        DockerClientConfig config = DefaultDockerClientConfig.
                createDefaultConfigBuilder()
                .build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
        PullImageResultCallback cb = new PullImageResultCallback();
        dockerClient.pullImageCmd("openjdk").withTag("12").exec(cb);
        cb.awaitCompletion();
        ExposedPort exposedRMI = ExposedPort.tcp(rmiPort);

        Ports portBindings = new Ports();
        portBindings.bind(exposedRMI, Ports.Binding.bindPort(rmiPort));
        CreateContainerResponse container = dockerClient.createContainerCmd("openjdk:12")
                .withBinds(new Bind(getJar().toPath().toAbsolutePath().getParent().toString(), new Volume("/safeNativeCode")))
                .withWorkingDir("/safeNativeCode")
                .withCmd(getJavaCommandArgs("java", false))
                .withNetworkMode("host")
                .exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        initialise();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dockerClient.stopContainerCmd(container.getId()).exec();
            dockerClient.removeContainerCmd(container.getId()).exec();
        }));
    }
}
