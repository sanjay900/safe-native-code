package slave;

import java.lang.reflect.InvocationTargetException;

public class SlaveMain {
    public static void main(String[] args) {
        try {
            new SlaveClassloader(Thread.currentThread().getContextClassLoader()).loadClass("slave.SlaveClient")
                    .getDeclaredConstructor(int.class)
                    .newInstance(Integer.parseInt(args[args.length - 1]));
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
