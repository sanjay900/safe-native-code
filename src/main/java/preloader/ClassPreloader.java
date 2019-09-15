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
        processor.handle();
        System.out.println("Preloaded " + loadedClasses.size() + " classes.");
    }
}


