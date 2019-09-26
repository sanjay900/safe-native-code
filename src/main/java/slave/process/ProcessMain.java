package slave.process;

import java.io.IOException;
import java.rmi.NotBoundException;

public class ProcessMain {
    public static void main(String[] args) throws InterruptedException, NotBoundException, IOException {
        new ProcessSlave(Integer.parseInt(args[args.length - 1]));
    }
}
