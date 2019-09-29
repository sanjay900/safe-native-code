package safeNativeCode.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Utils {
    /**
     * Read an Input Stream fully, and return its data as a byte array.
     * @param input the input stream to read
     * @return the data from the input stream as a byte array
     * @throws IOException an IOException occurred while reading the InputStream
     */
    public static byte[] readStream(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    /**
     * Check if a class is a core java class
     * @param name the name of the class
     * @return true if the class is a core java class, false otherwise
     */
    public static boolean isJavaClass(String name) {
        String className = name.replace(".", "/") + ".class";
        URL classLoc = ClassLoader.getSystemClassLoader().getResource(className);
        //jrt: = java9, java.home = java8
        return classLoc != null && (name.startsWith("java.") || classLoc.toString().startsWith("jar:file:" + System.getProperty("java.home")) || classLoc.toString().startsWith("jrt:/java.compiler") || classLoc.toString().startsWith("jrt:/java.base"));
    }

    /**
     * Check if a class is a JUnit or Gradle class, only if testing mode is enabled
     * @param name the name of the class to check
     * @return true if the class is a JUnit or Gradle class and testing mode is enabled, false otherwise
     */
    public static boolean isTestingClass(String name) {
        return (!"true".equals(System.getProperty("testing")) || (name.startsWith("org.junit.runners.Parameterized") || name.equals("org.junit.runner.RunWith") || name.equals("org.junit.Test") || name.contains("gradle")));
    }
}
