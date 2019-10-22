# SafeNativeCode

Safe Native Code is a project that aims to provide the ability to run native code in a memory safe way, by allowing for executing code inside another process transparently.

In order to support all safety features of this library, execute Java processes with the arguments `-Xshare:off -Djava.system.class.loader=safeNativeCode.SafeClassLoader`
