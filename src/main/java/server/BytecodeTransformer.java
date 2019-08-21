package server;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;

/**
 * A small java agent that collects up classes that are loaded so they can be shared with another process.
 */
public class BytecodeTransformer implements ClassFileTransformer {
    private HashMap<String, byte[]> classFiles;
    BytecodeTransformer(HashMap<String, byte[]> classFiles) {
        this.classFiles = classFiles;
    }

    /**
     * This HashMap stores all class files that are encountered
     */

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        //store this class file's content in classFiles, and structure its name so it is easily retrieved by rmi
        classFiles.put(className, classfileBuffer);
        return classfileBuffer;
    }
}
