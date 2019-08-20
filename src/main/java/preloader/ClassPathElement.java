package preloader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

class ClassPathElement {
    private static final Pattern JARFILE = Pattern.compile(".+\\.jar(\\.[0-9]+)?$", Pattern.CASE_INSENSITIVE);

    private URI path;

    ClassPathElement(URI path) {
        try {
            this.path = new URI(path.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    void handle(ClassPathCollector collector) {
        if (JARFILE.matcher(path.toString().trim()).matches()) {
            try (JarFile jar = new JarFile(new File(path))) {
                Enumeration<JarEntry> files = jar.entries();
                while (files.hasMoreElements()) {
                    JarEntry jarEntry = files.nextElement();
                    String name = jarEntry.getName().trim();
                    handleClasspathFile(collector, name);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //plain file or a directory
            File file = new File(path);
            handleFile(collector, file, true, null);
        }
    }

    private void handleFile(ClassPathCollector visitor, File file, boolean isTop, String path) {
        String name = file.getName().trim();
        String newPath = isTop ? null : path == null ? name : path + "/" + name;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                handleFile(visitor, f, false, newPath);
            }
        } else {
            handleClasspathFile(visitor, newPath);
        }
    }

    private void handleClasspathFile(ClassPathCollector visitor, String fileName) {
        if (fileName.endsWith(".class")) {
            if (fileName.equals("module-info.class") || fileName.startsWith("META-INF")) {
                return;
            }
            visitor.collect(new ClassFile(fileName));
        }
    }
}
