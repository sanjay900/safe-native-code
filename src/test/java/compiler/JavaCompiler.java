package compiler;

import javax.tools.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JavaCompiler {
    /**
     * Compile a class, and then return classToGet
     *
     * @param classToGet the class to get from the classpath
     * @return classToGet from the classpath, or null if you just want to compileAndGet (e.g. to make a TaskInfo)
     */
    public static Class<?> compileAndGet(String code, String classToGet) throws ClassNotFoundException {
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);
        ClassFileManager manager = new ClassFileManager(stdFileManager);
        List<JavaFileObject> compilationUnits = new ArrayList<>();
        compilationUnits.add(new MemorySourceFile(classToGet, code));
        List<String> compileOptions = new ArrayList<>();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        javax.tools.JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, manager, diagnostics, compileOptions, null, compilationUnits);
        boolean status = compilerTask.call();
        if (!status) {
            for (Diagnostic<? extends JavaFileObject> diag : diagnostics.getDiagnostics()) {
                if (diag.getSource() == null) {
                    continue;
                }
                String msg = diag.getMessage(Locale.getDefault());
                if (classToGet == null && !msg.contains("abstract")) {
                    System.err.println("Compilation error:\n" + diag.getMessage(Locale.getDefault()));
                }
                if (classToGet == null) {
                    continue;
                }
                System.out.println("Compilation error:\n" + diag.getMessage(Locale.getDefault()));
                throw new RuntimeException(diagnostics.getDiagnostics().toString());
            }
        }
        if (classToGet == null) return null;
        Class<?> clazz;
        try {
            clazz = manager.getClassLoader(null).loadClass(classToGet);
        } finally {
            for (JavaFileObject fileObject : compilationUnits) {
                fileObject.delete();
            }
        }
        return clazz;
    }
}
