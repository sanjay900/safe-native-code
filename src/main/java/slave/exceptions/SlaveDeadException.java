package slave.exceptions;


import slave.Slave;

import java.rmi.RemoteException;

/**
 * This exception is thrown if a slave has crashed or been shut down, and an attempt is made to use it.
 */
public class SlaveDeadException extends RemoteException {
    public SlaveDeadException(Slave slave) {
        super("Error: SlaveType " + slave.toString() + " is not running.");
    }
}
