package server.backends;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VagrantBackend extends ProcessBackend {
    private Path temp = Files.createTempDirectory("safeNativeCode-VM-");
    public VagrantBackend(boolean useAgent, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(useAgent, classLoaders);
        startProcess();
    }

    private void startProcess() throws IOException, InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                new ProcessBuilder("vagrant", "destroy", "-f").directory(temp.toFile()).start().waitFor();
                FileUtils.deleteDirectory(temp.toFile());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }));
        String vagrantConfig = String.join("\n", IOUtils.readLines(VagrantBackend.class.getResourceAsStream("/Vagrantfile"), "utf-8"));
        vagrantConfig = vagrantConfig.replaceAll("<port>", registryPort + "");
        vagrantConfig = vagrantConfig.replaceAll("<port2>", slavePort + "");
        vagrantConfig = vagrantConfig.replaceAll("<source>", getJar().getAbsolutePath());
        vagrantConfig = vagrantConfig.replaceAll("<javaCommand>", String.join(" ", getJavaCommandArgs("java", false, true)));
        Files.write(temp.resolve("Vagrantfile"), vagrantConfig.getBytes());
        new ProcessBuilder("vagrant", "up").directory(temp.toFile()).start();
        setupRegistry();
    }
    public boolean isAlive() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("vagrant", "status").directory(temp.toFile()).start();
        process.waitFor();
        return String.join("\n",IOUtils.readLines(process.getInputStream(), "utf-8")).contains("running");
    }
}
