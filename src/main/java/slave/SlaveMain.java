package slave;

import java.lang.reflect.InvocationTargetException;

public class SlaveMain {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // By loading this using the SlaveClassloader and reflection, we can correctly set up parent classloader, so that all class loading is done via the slave classloader.
        Class<?> clazz = new SlaveClassloader(Thread.currentThread().getContextClassLoader()).loadClass("slave.SlaveClient");
        clazz
                .getDeclaredConstructor(int.class, int.class, int.class, boolean.class)
                .newInstance(
                        Integer.parseInt(args[args.length - 4]),
                        Integer.parseInt(args[args.length - 3]),
                        Integer.parseInt(args[args.length - 2]),
                        args[args.length - 1].equals("true")
                );
    }
}
