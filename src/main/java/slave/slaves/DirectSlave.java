package slave.slaves;

import slaveProcess.SlaveProcessMain;

import java.io.IOException;

/**
 * A Direct Slave is a server that does absolutely nothing and just runs the code on the same JVM.
 * This is only for benchmarking, and isn't fully compatible as it can't handle classloaders.
 */
public class DirectSlave extends AbstractSlave {

    /**
     * Create a slave that runs inside this process
     *
     * @param classLoaders a list of classloaders to supply classes to the slave, if useAgent is false
     */
    public DirectSlave(ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(false, classLoaders);
        //At this point, we are pretending to be a slave. Start the slave components in another thread.
        Thread slaveInitThread = new Thread(() -> SlaveProcessMain.main(getSlaveArgs()));
        slaveInitThread.start();
        //Now set up the registry, knowing that it is being created above in another thread
        setupRegistry();
        //And wait for both the registry to be set up, and the slave's main thread to finish, as the slave
        //will start a new thread for execution.
        slaveInitThread.join();
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void terminate() {
        System.exit(0);
    }

    @Override
    public void waitForExit() throws InterruptedException {
        //Hopefully, the current JVM never dies, so lets just put this thread to sleep.
        Thread.currentThread().wait();
    }
}
