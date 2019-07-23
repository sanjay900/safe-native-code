package compiler;

import org.apache.commons.io.FilenameUtils;

class CompilerUtils {

    static String javaToURL(String str) {
        return str.replace(".", "/");
    }

    static String urlToJava(String str) {
        if (str.startsWith("/")) str = str.substring(1);
        return FilenameUtils.removeExtension(str.replace("/", "."));
    }
}
