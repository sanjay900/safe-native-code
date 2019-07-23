package server;

import sun.security.util.IOUtils;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;

import static spark.Spark.get;
import static spark.Spark.port;

/**
 * Start a web server hosting classes from a specific set of classloaders
 * Also supports being run in agent mode. This is useful if Classes are loaded dynamically, and the method for loading these classes dynamically does not expose raw byte code.
 */
public class BytecodeServer implements ClassFileTransformer {
    /**
     * This HashMap stores all class files that are encountered
     */
    private static HashMap<String, byte[]> classFiles = new HashMap<>();

    public static void agentmain(String args, Instrumentation instrumentation) {
        //Add a transformer that simply stores all classes encountered to classFiles
        instrumentation.addTransformer(new BytecodeServer(Integer.parseInt(args), ClassLoader.getSystemClassLoader()));
    }

    public BytecodeServer(int port, ClassLoader... classLoaders) {
        port(port);
        //Start a webserver that serves any class from classFiles
        get("/*", (req, res) -> {
            if (classFiles.containsKey(req.pathInfo())) {
                return classFiles.get(req.pathInfo());
            }
            String path = req.pathInfo().substring(1);
            for (ClassLoader loader : classLoaders) {
                InputStream is = loader.getResourceAsStream(path);
                if (is != null) {
                    return IOUtils.readFully(is, Integer.MAX_VALUE, false);
                }
            }
            return null;
        });
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        //store this class file's content in classFiles, and structure its name so it is easily retrieved by rmi
        classFiles.put("/" + className + ".class", classfileBuffer);
        return classfileBuffer;
    }
}
