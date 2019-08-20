package server.servers;

import slave.SlaveMain;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 * A Direct Server is a server that does absolutely nothing and just runs the code on the same JVM.
 * This is only for benchmarking, and isn't fully compatible as it can't handle classloaders.
 */
public class DirectServer extends AbstractServer {
    /**
     * Direct server ignores all arguments, but keeps these to be compatible.
     *
     * @param agent        ignored
     * @param classLoaders ignored
     */
    public DirectServer(boolean agent, ClassLoader... classLoaders) throws IOException, InterruptedException {
        super(agent, false, classLoaders);
        //At this point, we are pretending to be a slave. Start the slave components in another thread.
        Thread t = new Thread(() -> {
            try {
                SlaveMain.constructSlave(getSlaveArgs(false));
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        //Now set up the registry, knowing that it is being created above in another thread
        try {
            setupRegistry();
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
        //And wait for both the registry to be set up, and the slave's main thread to finish, as the slave
        //will start a new thread for execution.
        t.join();
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public void waitForExit() {
        //Hopefully, the current JVM never dies, so lets just leave a while loop here so that threads waiting on this
        //for cleanup don't cleanup early.
    }
}
