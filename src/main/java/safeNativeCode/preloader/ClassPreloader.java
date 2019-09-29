package safeNativeCode.preloader;

import safeNativeCode.SafeClassLoader;

import java.util.Set;

public class ClassPreloader {
    private ClassPathProcessor processor;
    private Set<String> loadedClasses;


    public ClassPreloader() {
        processor = new ClassPathProcessor();
    }

    public void preload(SafeClassLoader safeCodeLibrary, Set<String> loaded) {
        this.loadedClasses = loaded;
        //Load all classes that the processor visits.
        processor.handle(classFile -> {
            String className = classFile.getClassName();
            if (!loadedClasses.contains(className)) {
                loadedClasses.add(className);
                //Try and load classes. Some classes will fail, this is normal as some libraries specify optional
                //dependencies that we do not actually have.
                try {
                    Class.forName(className, true, safeCodeLibrary);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                }
            }
        });
        System.out.println("Preloaded " + loadedClasses.size() + " classes.");
    }
}


