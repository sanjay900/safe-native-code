package preloader;

import java.util.HashSet;
import java.util.Set;

public class ClassPreloader {
    private ClassPathProcessor processor;
    private Set<String> loadedClasses;


    public ClassPreloader() {
        this.loadedClasses = new HashSet<>();
        processor = new ClassPathProcessor();
    }

    public void preload() {
        //Load all classes that the processor visits.
        processor.handle(classFile -> {
            String className = classFile.getClassName();
            if (!loadedClasses.contains(className)) {
                loadedClasses.add(className);
                //Try and load classes. Some classes will fail, this is normal as some libraries specify optional
                //dependencies that we do not actually have.
                try {
                    Class.forName(className, false, ClassLoader.getSystemClassLoader());
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                }
            }
        });
        System.out.println("Preloaded " + loadedClasses.size() + " classes.");
        //TODO: disable loading other classes
    }
}


