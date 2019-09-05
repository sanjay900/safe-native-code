package slave.process;

import java.lang.reflect.InvocationTargetException;

public class SlaveProcessMain {
    public static void main(String[] args) {
        try {
            new SlaveProcessClassloader(Thread.currentThread().getContextClassLoader()).loadClass("slave.process.SlaveProcessClient")
                    .getDeclaredConstructor(int.class)
                    .newInstance(Integer.parseInt(args[args.length - 1]));
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
