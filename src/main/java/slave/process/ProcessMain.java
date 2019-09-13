package slave.process;

import java.lang.reflect.InvocationTargetException;

public class ProcessMain {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        new ProcessClassloader(Thread.currentThread().getContextClassLoader()).loadClass(ProcessSlave.class.getName())
                .getDeclaredConstructor(int.class)
                .newInstance(Integer.parseInt(args[args.length - 1]));
    }
}
