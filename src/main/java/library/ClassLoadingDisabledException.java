package library;

public class ClassLoadingDisabledException extends ClassNotFoundException {
    public ClassLoadingDisabledException() {
        super("Class loading has been disabled for security reasons");
    }
}
