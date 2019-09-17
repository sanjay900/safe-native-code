package slave;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Utils {
    public static byte[] readStream(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public static boolean isJavaClass(String name) {
        String className = name.replace(".", "/") + ".class";
        URL classLoc = ClassLoader.getSystemClassLoader().getResource(className);
        //jrt: = java9, java.home = java8
        return classLoc != null && (name.startsWith("java.") || classLoc.toString().startsWith("jar:file:" + System.getProperty("java.home")) || classLoc.toString().startsWith("jrt:/java.compiler") || classLoc.toString().startsWith("jrt:/java.base"));
    }

    public static boolean isTestingClass(String name) {
        return (!"true".equals(System.getProperty("testing")) || (name.equals("org.junit.runners.Parameterized") || name.equals("org.junit.runner.RunWith") || name.equals("org.junit.Test") || name.contains("gradle")));
    }
}
