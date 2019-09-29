package safeNativeCode.slave;

import java.io.IOException;

/**
 * A Slave is a remote process that is able to execute code
 */
public interface Slave extends InternalSlave {

    /**
     * Is this slave still alive
     *
     * @return true if the server is slave alive false otherwise
     */
    boolean isAlive() throws IOException, InterruptedException;

    /**
     * Ask the slave to terminate
     */
    void terminate();

    /**
     * Wait for the slave to exit
     */
    void waitForExit() throws InterruptedException, IOException;
}
