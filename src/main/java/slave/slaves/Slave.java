package slave.slaves;

import shared.SharedAPI;

import java.io.IOException;

/**
 * A Slave is a type of service that can command a slave of some sort.
 */
public interface Slave extends SharedAPI {

    /**
     * Is this server still alive
     * @return true if the server is still alive and connected to a client, false otherwise
     */
    boolean isAlive() throws IOException, InterruptedException;

    /**
     * Ask the client to terminate
     */
    void terminate();

    /**
     * Wait for the client to exit
     */
    void waitForExit() throws InterruptedException, IOException;
}
