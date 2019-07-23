package compiler;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.Collections;
import java.util.List;

public class JavaCompiler {
    private static ClassFileManager manager = new ClassFileManager(ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null));

    public static Class<?> compile(String code, String className) {
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<JavaFileObject> compilationUnits = Collections.singletonList(new MemorySourceFile(className, code));
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        javax.tools.JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, manager, diagnostics, null, null, compilationUnits);
        boolean status = compilerTask.call();
        if (!status) {
            throw new RuntimeException(diagnostics.getDiagnostics().toString());
        }
        try {
            return manager.getClassLoader(null).loadClass(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static ClassLoader getClassLoader() {
        return manager.getClassLoader(null);
    }
}
