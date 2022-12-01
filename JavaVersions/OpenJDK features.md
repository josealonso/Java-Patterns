## [Java 9](https://www.oracle.com/java/technologies/javase/9all-relnotes.html)

### **Most important changes**

- **Modular System – Jigsaw Project**

The JVM is modular, so it can run on devices with a lot less available memory. The JVM can run with only those modules and APIs which are required by the application. 

The modules are going to be described in a file called *module-info.java* located in the top of java code hierarchy.

- **A new HTTP Client**

It supports both [**HTTP/2 protocol**](https://http2.github.io/) and **WebSocket** handshake, with performance that should be comparable with the Apache HttpClient, Netty and Jetty.
The API uses the Builder pattern.

```
HttpRequest request = HttpRequest.newBuilder()
  .uri(new URI("https://postman-echo.com/get"))
  .GET()
  .build();

HttpResponse<String> response = HttpClient.newHttpClient()
  .send(request, HttpResponse.BodyHandler.asString());
```
- **JShell Command Line Tool**

JShell is read–eval–print loop – REPL for short.

It's an interactive tool to evaluate declarations, statements, and expressions of Java, together with an API. It is very convenient for testing small code snippets, which otherwise require creating a new class with the main method.

- **Improved *Process* API**

The *current* method returns an object representing a process of currently running JVM. The *Info* subclass provides details about the process.

```
ProcessHandle self = ProcessHandle.current();
long PID = self.getPid();
ProcessHandle.Info procInfo = self.info();
 
Optional<String[]> args = procInfo.arguments();
Optional<String> cmd =  procInfo.commandLine();
Optional<Instant> startTime = procInfo.startInstant();
Optional<Duration> cpuUsage = procInfo.totalCpuDuration();
```

```
// stop all the running child processes using destroy()

childProc = ProcessHandle.current().children();
childProc.forEach(procHandle -> {
    assertTrue("Could not kill process " + procHandle.getPid(), procHandle.destroy());
});
```

This is similar to Unix system calls.

- **Immutable Factory methods**

*java.util.List.of()* creates an immutable list of the given elements. In Java 8 creating a List of several elements would require several lines of code. Now we can do it as simple as:

```
List<String> usersList = List.of("Alice", Bob");
usersList.add("John");       // compile error
```

- **Private methods in interfaces**

Interfaces can have private methods, which can be used to split lengthy default methods:

```
interface InterfaceWithPrivateMethods {
    
    private static String staticPrivate() {
        return "static private";
    }
    
    private String instancePrivate() {
        return "instance private";
    }
    
    default void check() {
        String result = staticPrivate();
        InterfaceWithPrivateMethods pvt = new InterfaceWithPrivateMethods() {
            // anonymous class
        };
        result = pvt.instancePrivate();
    }
}}
```


- **Enhanced *Completable Future* API**

- **New methods in the `Optional` API**

*java.util.Optional.stream()* allows to you use the power of Streams on Optional elements:

```
List<String> filteredList = listOfOptionals.stream()
  .flatMap(Optional::stream)
  .collect(Collectors.toList());
```


- **Variable Handles**

The API resides under *java.lang.invoke* and consists of *VarHandle* and *MethodHandles*. It provides equivalents of *java.util.concurrent.atomic* and *sun.misc.Unsafe* operations upon object fields and array elements with similar performance.

**With Java 9 Modular system access to sun.misc.Unsafe will not be possible from application code.**

- **Publish-Subscribe Framework**

The class *java.util.concurrent.Flow* provides interfaces that support the [Reactive Streams](http://www.reactive-streams.org/) publish-subscribe framework. These interfaces support interoperability across a number of asynchronous systems running on JVMs.

We can use the utility class *SubmissionPublisher* to create custom components.

- **Unified JVM Logging**

============================================================

## [Java 10](https://www.oracle.com/java/technologies/javase/10all-relnotes.html)

Java SE 10, was released on March 20, 2018.

### **Most important changes**

- **Local Variable Type Inference**

```var list = new ArrayList<String>();    // infers ArrayList<String>```

- *var* is a reserved type name, not a keyword, which means that existing code that uses var as a variable, method, or package name is not affected. However, code that uses var as a class or interface name is affected and the class or interface needs to be renamed.

- **Unmodifiable Collections**

*copyOf()* and *toUnmodifiable*

```
// copyOf() returns the unmodifiable copy of the given Collection

@Test(expected = UnsupportedOperationException.class)
public void whenModifyCopyOfList_thenThrowsException() {
    List<Integer> copyList = List.copyOf(integersList);
    copyList.add(4);
}
```

```
// java.util.stream.Collectors get additional methods to collect a Stream into an unmodifiable List, Map or Set

@Test(expected = UnsupportedOperationException.class)
public void whenModifyToUnmodifiableList_thenThrowsException() {
    List<Integer> evenList = someIntList.stream()
      .filter(i -> i % 2 == 0)
      .collect(Collectors.toUnmodifiableList());
    evenList.add(4);
}
```

- **Optional.orElseThrow() Method**

A new method `orElseThrow` has been added to the `Optional` class. It is synonymous with and **is now the preferred alternative to the existing get() method.**

```
@Test
public void whenListContainsInteger_OrElseThrowReturnsInteger() {
    Integer firstEven = someIntList.stream()
      .filter(i -> i % 2 == 0)
      .findFirst()
      .orElseThrow();
    is(firstEven).equals(Integer.valueOf(2));
}
```

- **Performance Improvements**

- **Container Awareness**

**JVMs are now aware of being run in a Docker container** and will extract container-specific configuration instead of querying the operating system itself.

This support is **only** available for **Linux-based platforms**. This new support is enabled by default and can be disabled in the command line with the JVM option:

`-XX:-UseContainerSupport`

Also, this change adds a JVM option that provides the ability to specify the number of CPUs that the JVM will use:

```
-XX:ActiveProcessorCount=count
```

Three new JVM options have been added for Docker container users to control the amount of system memory that will be used for the Java Heap:

```
-XX:InitialRAMPercentage
-XX:MaxRAMPercentage
-XX:MinRAMPercentage
```

- **Root Certificates**

The *cacerts* keystore, which was initially empty so far, is intended to contain a set of root certificates that can be used to establish trust in the certificate chains used by various security protocols.

As a result, critical security components such as TLS didn't work by default under OpenJDK builds.

**With Java 10, Oracle has open-sourced the root certificates** in Oracle's Java SE Root CA program in order to make OpenJDK builds more attractive to developers and to reduce the differences between those builds and Oracle JDK builds.

- **Hashed Passwords for Out-of-the-Box JMX Agent**

The clear passwords present in the `jmxremote.password` file are now being over-written with their SHA3-512 hash by the JMX agent. 

- **Improved Garbage Collector**

Improves G1 worst-case latencies by making the full GC parallel. The G1 garbage collector is designed to avoid full collections, but when the concurrent collections can't reclaim memory fast enough a fall back full GC will occur. The old implementation of the full GC for G1 used a single threaded mark-sweep-compact algorithm. With JEP 307 **the full GC has been parallelized** and now use the same amount of parallel worker threads as the young and mixed collections.

- **Bytecode Generation for Enhanced for Loop**

- Bytecode generation has been improved for enhanced for loops, providing an improvement in the translation approach for them. For example:

`List<String> data = new ArrayList<>(); for (String b : data);`

The following is the code generated after the enhancement:
```
{ /*synthetic*/ Iterator i$ = data.iterator(); for (; i$.hasNext(); ) { String b = (String)i$.next(); } b = null; i$ = null; }
```

- Declaring the iterator variable outside of the for loop allows a null to be assigned to it as soon as it is no longer used. This makes it accessible to the GC, which can then get rid of the unused memory. Something similar is done for the case when the expression in the enhanced for loop is an array.

P L U S 

- **A number of removals**

- **Time-Based Release Versioning**

Starting with Java 10, Oracle has moved to the time-based release of Java. This has following implications:

A new Java release every six months. The March 2018 release is JDK 10, the September 2018 release is JDK 11, and so forth. These are called feature releases and are expected to contain at least one or two significant features.
Support for the feature release will last only for six months, i.e., until next feature release.
**LTS releases will be supported for for three years.**

=======================================================================

## [Java 11](https://www.oracle.com/java/technologies/javase/11all-relnotes.html)

### **Most important changes**

**Improved Local Variable Type Inference**

- Introduced in Java SE 10. In this release, it has been enhanced with support for **allowing *var* to be used when declaring the formal parameters of implicitly typed lambda expressions.**


core-libs/java.net
➜ **Make HttpURLConnection Default Keep Alive Timeout Configurable**

- Two system properties have been added which control the keep alive behavior of HttpURLConnection in the case where the server does not specify a keep alive time. Two properties are defined for controlling connections to servers and proxies separately. They are `http.keepAlive.time.server` and `http.keepAlive.time.proxy ` respectively. 

hotspot/runtime
➜ **CPU Shares Ignored When Computing Active Processor Count**

- Previous JDK releases used an incorrect interpretation of the Linux cgroups parameter *cpu.shares*. This might cause the JVM to use fewer CPUs than available, leading to an under utilization of CPU resources when the JVM is used inside a container.

- Starting from this JDK release, by default, the JVM no longer considers *cpu.shares* when deciding the number of threads to be used by the various thread pools. 

xml/jaxp
➜ **New XML Processing Limits**

    - Three processing limits have been added to the XML libraries. These are:
    
    jdk.xml.xpathExprGrpLimit

    jdk.xml.xpathExprOpLimit

    jdk.xml.xpathTotalOpLimit

- For the XSLT processor, the properties can be changed through the TransformerFactory. For example,

```
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setAttribute("jdk.xml.xpathTotalOpLimit", "1000");
```

- For both the XPath and XSLT processors, the properties can be set through the system property and jaxp.properties configuration file located in the conf directory of the Java installation. For example,

    `System.setProperty("jdk.xml.xpathExprGrpLimit", "20");`

or in the jaxp.properties file,

    `jdk.xml.xpathExprGrpLimit=20`

security-libs/javax.net.ssl
➜ **Disable TLS 1.0 and 1.1**

TLS 1.0 and 1.1 are versions of the TLS protocol that are no longer considered secure and have been superseded by more secure and modern versions (TLS 1.2 and 1.3).

These versions have now been disabled by default. If you encounter issues, you can, at your own risk, re-enable the versions by removing "TLSv1" and/or "TLSv1.1" from the jdk.tls.disabledAlgorithms security property in the java.security configuration file.

core-libs/java.util:collections
➜ **Better Listing of Arrays**

The preferred way to copy a collection is to use a *copy constructor.* For example, to copy a collection into a new ArrayList, one would write `new ArrayList<>(collection)`. In certain circumstances, an additional, temporary copy of the collection's contents might be made in order to improve robustness. If the collection being copied is exceptionally large, then the application should be (aware of/monitor) the significant resources required involved in making the copy.

core-svc/java.lang.management
➜ **OperatingSystemMXBean Methods Inside a Container Return Container Specific Data**
When executing in a container, or other virtualized operating environment, the following OperatingSystemMXBean methods in this release return container specific information, if available. Otherwise, they return host specific data:

    getFreePhysicalMemorySize()
    getTotalPhysicalMemorySize()
    getFreeSwapSpaceSize()
    getTotalSwapSpaceSize()
    getSystemCpuLoad()

security-libs/java.security

➜ **Added 4 Amazon Root CA Certificates**
The following root certificates have been added to the cacerts truststore:

+ Amazon
  + amazonrootca1
    DN: CN=Amazon Root CA 1, O=Amazon, C=US

  + amazonrootca2
    DN: CN=Amazon Root CA 2, O=Amazon, C=US

  + amazonrootca3
    DN: CN=Amazon Root CA 3, O=Amazon, C=US

  + amazonrootca4
    DN: CN=Amazon Root CA 4, O=Amazon, C=US

core-libs/java.util
➜ **Changed Properties.loadFromXML to Comply with Specification**

- The implementation of the java.util.Properties.loadFromXML method has been changed to comply with its specification. Specifically, the underlying XML parser implementation now rejects non-compliant XML documents by throwing an InvalidPropertiesFormatException as specified by the loadFromXML method.

The effect of the change is as follows:

    Documents created by Properties.storeToXML: No change. Properties.loadFromXML will have no problem reading such files.

    Documents not created by Properties.storeToXML: Any documents containing DTDs not in the format as specified in Properties.loadFromXML will be rejected. This means the DTD shall be exactly as follows (as generated by the Properties.storeToXML method):
    ```
    <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
    ```




















