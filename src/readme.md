#SafeNativeCode

Running securely

Please run java processes with the arguments -Xshare:off -Djava.system.class.loader=library.SafeCodeLibrary
Note that for junit support, a -Dtesting=true needs to also be passed, as junit and gradle don't behave correctly through our classloader,
but blocking it would leave a security vulnerability
