package slave;

import java.lang.reflect.InvocationTargetException;

public class SlaveMain {
    public static void main(String[] args) {
        // By loading this using the SlaveClassloader and reflection, we can correctly set up parent classloader, so that all class loading is done via the slave classloader.
        constructSlave(args);
    }

    public static void constructSlave(String[] args) {
        try {
            new SlaveClassloader(Thread.currentThread().getContextClassLoader()).loadClass("slave.SlaveClient")
                    .getDeclaredConstructor(int.class, int.class, int.class, boolean.class)
                    .newInstance(
                            Integer.parseInt(args[args.length - 4]),
                            Integer.parseInt(args[args.length - 3]),
                            Integer.parseInt(args[args.length - 2]),
                            args[args.length - 1].equals("true")
                    );
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
