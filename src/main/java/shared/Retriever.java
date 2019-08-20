package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A simple interface that allows for asking a remote process to do things
 * it can ask a remote process to hand over bytecode for a class
 * and it can ask a remote process to print to stdout and stderr
 */
public interface Retriever extends Remote {
    /**
     * Retrieve the bytecode for a class
     * @param clazz the name of a class to retrieve bytecode for
     * @return the retrieved bytecode, or null if it does not exist
     */
    byte[] getByteCode(String clazz) throws RemoteException;

    /**
     * Print a character to stdout
     * @param i the character to print
     */
    void printOut(int i) throws RemoteException;

    /**
     * Print a character to stderr
     * @param i the character to print
     */
    void printErr(int i) throws RemoteException;
}
