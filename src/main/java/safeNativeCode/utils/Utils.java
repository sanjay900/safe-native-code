package safeNativeCode.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.BiPredicate;

public class Utils {
    /**
     * Read an Input Stream fully, and return its data as a byte array.
     *
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
     *
     * @param name the name of the class
     * @return true if the class is a core java class, false otherwise
     */
    public static boolean isJavaClass(String name) {
        String className = name.replace(".", File.separator) + ".class";
        URL classLoc = ClassLoader.getSystemClassLoader().getResource(className);
        return isJavaClass.test(classLoc,name);
    }
    /**
     * Get the current customized check for core java classes
     * @return BiPredicate<URL,String>
     */
    static public BiPredicate<URL,String> getIsJavaClass(){return isJavaClass;}
    
    /**
     * Set the current customized check for core java classes.
     * The corresponding get method can be used to emulate super.
     * @parameter BiPredicate<URL,String>
     */
    static public void setIsJavaClass(BiPredicate<URL,String> test){isJavaClass=test;}
    static private volatile BiPredicate<URL,String> isJavaClass=(classLoc,name)->defaultIsJavaClass(classLoc,name);
    private static boolean defaultIsJavaClass(URL classLoc,String name){
        //jrt: = java9, java.home = java8
        return classLoc != null && (name.startsWith("java.") || classLoc.toString().startsWith("jar:file:" + System.getProperty("java.home")) || classLoc.toString().startsWith("jrt:/java.compiler") || classLoc.toString().startsWith("jrt:/java.base") || classLoc.toString().startsWith("jrt:/java.desktop"));
    }
    
    /**
     * Check if a class is a JUnit or Gradle class, only if testing mode is enabled
     *
     * @param name the name of the class to check
     * @return true if the class is a JUnit or Gradle class and testing mode is enabled, false otherwise
     */
    public static boolean isTestingClass(String name) {
        return (!"true".equals(System.getProperty("testing")) || (name.startsWith("org.junit.runners.Parameterized") || name.equals("org.junit.runner.RunWith") || name.equals("org.junit.Test") || name.contains("gradle")));
    }


    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }
}
