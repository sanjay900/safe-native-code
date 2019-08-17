/* Copyright (c) 2014 Raymond Xu
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software.*/
package preloader.loader;

import preloader.classpath.ClassPathProcessor;
import preloader.classpath.element.ClassFile;
import preloader.classpath.visitor.ClassPathVisitorAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * based on https://github.com/jermainexu/ClassPreloader
 */
public class ClassPreloader {
    private ClassPathProcessor processor;
    private Set<String> loadedClasses;


    public ClassPreloader() {
        this.processor = new ClassPathProcessor(Thread.currentThread().getContextClassLoader());
        this.loadedClasses = new HashSet<>();
    }

    public void preload() {
        processor.accept(new PreloaderVisitor());
    }

    class PreloaderVisitor extends ClassPathVisitorAdapter {

        @Override
        public boolean visit(ClassFile classFile) {
            String className = classFile.getClassName();
            if (!loadedClasses.contains(className)) {
                loadedClasses.add(className);
                try {
                    processor.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    return true;
                }
                return true;
            }
            return false;
        }
    }
}


