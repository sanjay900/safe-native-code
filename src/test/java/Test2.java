import compiler.JavaCompiler;
import server.RemoteCodeManager;
import server.Slave;
import test.TestIntf;

public class Test2 {
    public static void main(String[] args) throws Exception {
        RemoteCodeManager.InitialiseRemoteCode(1234,1235);
        JavaCompiler.compile(
                "package test; import java.io.Serializable;" +
                        "public class Test implements Serializable, TestIntf {" +
                        "public void printSomething() {System.out.println(\"test\");}" +
                        "}", "memes.Test");
        Slave slave = new Slave(System.getProperty("java.class.path"));
        Class<?> clazz = JavaCompiler.getClassLoader().loadClass("test.Test");
        slave.call(() -> {
            try {
                TestIntf i = (TestIntf) clazz.newInstance();
                i.printSomething();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return "";
        });
    }
}
