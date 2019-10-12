package safeNativeCode.preloader;

import java.io.File;

class ClassFile {
    private String fileName;

    ClassFile(String fileName){
        this.fileName = fileName;
    }

    String getClassName(){
        return fileName.substring(0, fileName.lastIndexOf(".class")).replace(File.separator, ".");
    }

}
