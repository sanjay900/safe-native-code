package safeNativeCode.preloader;

class ClassFile {
    private String fileName;

    ClassFile(String fileName){
        this.fileName = fileName;
    }

    String getClassName(){
        return fileName.substring(0, fileName.lastIndexOf(".class")).replace("/", ".");
    }

}
