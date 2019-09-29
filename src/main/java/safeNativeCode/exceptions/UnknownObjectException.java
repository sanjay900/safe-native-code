package safeNativeCode.exceptions;

/**
 * This exception is thrown if a RemoteObject is passed into an incorrect safeNativeCode.slave, or if an object is used after deletion.
 */
public class UnknownObjectException extends RuntimeException {
}
