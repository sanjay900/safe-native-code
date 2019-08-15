package server.backends;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VagrantBackend extends ProcessBackend {
    public VagrantBackend(int rmiPort) throws IOException, InterruptedException {
        super(rmiPort);
        startProcess();
    }

    public VagrantBackend(int rmiPort, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(rmiPort, classLoaders);
        startProcess();
    }

    private void startProcess() throws IOException, InterruptedException {
        Path temp = Files.createTempDirectory("saveNativeCode-VM-");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                new ProcessBuilder("vagrant", "destroy", "-f").directory(temp.toFile()).inheritIO().start().waitFor();
                FileUtils.deleteDirectory(temp.toFile());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }));
        String vagrantConfig = String.join("\n", IOUtils.readLines(VagrantBackend.class.getResourceAsStream("/Vagrantfile"), "utf-8"));
        vagrantConfig = vagrantConfig.replaceAll("<port>", rmiPort + "");
        vagrantConfig = vagrantConfig.replaceAll("<port2>", rmiPort + 2 + "");
        vagrantConfig = vagrantConfig.replaceAll("<source>", getJar().getAbsolutePath());
        vagrantConfig = vagrantConfig.replaceAll("<javaCommand>", String.join(" ", getJavaCommandArgs("java", false)));
        Files.write(temp.resolve("Vagrantfile"), vagrantConfig.getBytes());
        new ProcessBuilder("vagrant", "up").directory(temp.toFile()).inheritIO().start();
        System.out.println("Starting up Vagrant VM...");
        initialise();
        System.out.println("Vagrant VM started and connected.");
    }
}
