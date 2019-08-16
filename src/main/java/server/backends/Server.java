package server.backends;

import shared.SlaveAPI;

import java.io.IOException;

/**
 * A Server is a type of service that can command a slave of some sort.
 */
public interface Server extends SlaveAPI {
    boolean isAlive() throws IOException, InterruptedException;

    void exit();

    void waitForExit() throws InterruptedException, IOException;
}
