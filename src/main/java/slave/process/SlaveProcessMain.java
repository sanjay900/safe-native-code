package slave.process;

import java.lang.reflect.InvocationTargetException;

public class SlaveProcessMain {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        new SlaveProcessClassloader(Thread.currentThread().getContextClassLoader()).loadClass("slave.process.SlaveProcessClient")
                .getDeclaredConstructor(int.class)
                .newInstance(Integer.parseInt(args[args.length - 1]));
    }
}
