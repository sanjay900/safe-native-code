package preloader;

import java.io.File;
import java.util.Arrays;

public class ClassPathProcessor {
    void handle(ClassPathCollector collector) {
        Arrays
                .stream(System.getProperty("java.class.path").split(":"))
                .map(s -> new File(s).toURI())
                .distinct()
                .forEach(element -> new ClassPathElement(element).handle(collector));
    }
}
