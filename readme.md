#SafeNativeCode

Running securely

Please run java processes with the arguments -Xshare:off -Djava.system.class.loader=safeNativeCode.SafeClassLoader
Note that for junit support, a -Dtesting=true needs to also be passed, as junit and gradle don't behave correctly through our classloader,
but blocking it would leave a security vulnerability. This is something that is built into the build.gradle script.
