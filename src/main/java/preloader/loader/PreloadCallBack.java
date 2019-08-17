/* Copyright (c) 2014 Raymond Xu
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software.*/
package preloader.loader;

/**
 * based on https://github.com/jermainexu/ClassPreloader
 */
public interface PreloadCallBack {
    void classLoaded(String className);
}
