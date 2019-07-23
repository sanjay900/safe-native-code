package compiler;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.ArrayList;
import java.util.List;

public class JavaCompiler {
    private static ClassFileManager manager = new ClassFileManager(ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null));

    public static void compile(String code, String className) {
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<JavaFileObject> compilationUnits = new ArrayList<>();
        compilationUnits.add(new MemorySourceFile(className, code));
        List<String> compileOptions = new ArrayList<>();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        javax.tools.JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, manager, diagnostics, compileOptions, null, compilationUnits);
        boolean status = compilerTask.call();
        if (!status) {
            throw new RuntimeException(diagnostics.getDiagnostics().toString());
        }
    }

    public static ClassLoader getClassLoader() {
        return manager.getClassLoader(null);
    }
}
