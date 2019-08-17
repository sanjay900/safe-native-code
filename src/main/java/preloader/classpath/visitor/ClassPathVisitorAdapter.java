/* Copyright (c) 2014 Raymond Xu
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software.*/
package preloader.classpath.visitor;

import preloader.classpath.ClassPathProcessor;
import preloader.classpath.element.ClassFile;
import preloader.classpath.element.ClassPathElement;

/**
 * based on https://github.com/jermainexu/ClassPreloader
 */
public class ClassPathVisitorAdapter implements ClassPathVisitor {
    @Override
    public boolean visitEnter(ClassPathElement cpElement) {
        return true;
    }

    @Override
    public boolean visitLeave(ClassPathElement cpElement) {
        return true;
    }

    @Override
    public boolean visit(ClassFile classFile) {
        return true;
    }

    @Override
    public boolean visit(ClassPathProcessor processor) {
        return true;
    }
}
