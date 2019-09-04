package shared;

public class SlaveException extends RuntimeException {
    private RemoteObject<Throwable> child;

    public SlaveException(RemoteObject<Throwable> child) {
        this.child = child;
    }

    public RemoteObject<Throwable> getChild() {
        return child;
    }
}
