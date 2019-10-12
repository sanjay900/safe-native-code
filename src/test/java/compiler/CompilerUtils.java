package compiler;

import java.io.File;

class CompilerUtils {

    static String javaToURL(String str) {
        return str.replace(".", File.separator);
    }

    static String urlToJava(String str) {
        if (str.startsWith(File.separator)) str = str.substring(1);
        return str.replace(File.separator, ".");
    }
}
