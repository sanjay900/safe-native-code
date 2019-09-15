package preloader;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

class ClassPathProcessor {
    void handle() {
        Arrays
                .stream(System.getProperty("java.class.path").split(":"))
                .map(s -> new File(s).toURI())
                .distinct()
                .forEach(element -> new ClassPathElement(element).handle());
    }
}
