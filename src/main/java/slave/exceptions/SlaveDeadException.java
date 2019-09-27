package slave.exceptions;


import slave.SlaveInternal;

import java.rmi.RemoteException;

/**
 * This exception is thrown if a slave has crashed or been shut down, and an attempt is made to use it.
 */
public class SlaveDeadException extends RemoteException {
    public SlaveDeadException(SlaveInternal slave) {
        super("Error: Slave " + slave + " is not running.");
    }
}
