package slave.types;

import slave.process.SlaveProcessMain;

import java.io.IOException;

/**
 * A Direct SlaveType is a server that does absolutely nothing and just runs the code on the same JVM.
 * This is only for benchmarking, and isn't fully compatible as it can't handle classloaders.
 */
public class DirectSlave extends AbstractSlave {

    /**
     * Create a slave that runs inside this process
     *
     * @param classLoaders a list of classloaders to supply classes to the slave, if useAgent is false
     */
    public DirectSlave(ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(classLoaders);
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
        //Terminating the current JVM isn't a good idea
    }

    @Override
    public void waitForExit() throws InterruptedException {
        //Since waitForExit is called in the same JVM, this call is either waiting, or the process isn't running
        //Since there isn't a case where we can continue past here, lets just wait forever.
        while (true) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}
