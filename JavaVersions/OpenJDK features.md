
## [Java 10](https://www.oracle.com/java/technologies/javase/10all-relnotes.html)

### **Most important changes**

**Local Variable Type Inference**

```var list = new ArrayList<String>();    // infers ArrayList<String>```

- *var* is a reserved type name, not a keyword, which means that existing code that uses var as a variable, method, or package name is not affected. However, code that uses var as a class or interface name is affected and the class or interface needs to be renamed.

core-libs/java.util
➜ **Optional.orElseThrow() Method**

- A new method `orElseThrow` has been added to the `Optional` class. It is synonymous with and is now the preferred alternative to the existing `get` method.

core-libs/java.util:collections
➜ **APIs for Creating Unmodifiable Collections**

- Several new APIs have been added that facilitate the creation of unmodifiable collections. The `List.copyOf, Set.copyOf`, and `Map.copyOf` methods create new collection instances from existing instances. New methods `toUnmodifiableList, toUnmodifiableSet`, and `toUnmodifiableMap` have been added to the `Collectors` class in the Stream package. These allow the elements of a Stream to be collected into an unmodifiable collection.

core-svc/javax.management
➜ **Hashed Passwords for Out-of-the-Box JMX Agent**

- The clear passwords present in the `jmxremote.password` file are now being over-written with their SHA3-512 hash by the JMX agent. 

hotspot/gc
➜ **JEP 307 Parallel Full GC for G1**

- Improves G1 worst-case latencies by making the full GC parallel. The G1 garbage collector is designed to avoid full collections, but when the concurrent collections can't reclaim memory fast enough a fall back full GC will occur. The old implementation of the full GC for G1 used a single threaded mark-sweep-compact algorithm. With JEP 307 the full GC has been parallelized and now use the same amount of parallel worker threads as the young and mixed collections.

security-libs/java.security
➜ **JEP 319 Root Certificates**

- Provides a default set of root Certification Authority (CA) certificates in the JDK.

security-libs/javax.net.ssl
➜ **TLS Session Hash and Extended Master Secret Extension Support**

- Support has been added for the TLS session hash and extended master secret extension (RFC 7627) in JDK JSSE provider. 

tools/javac
➜ **Bytecode Generation for Enhanced for Loop**

- Bytecode generation has been improved for enhanced for loops, providing an improvement in the translation approach for them. For example:

`List<String> data = new ArrayList<>(); for (String b : data);`

The following is the code generated after the enhancement:
```
{ /*synthetic*/ Iterator i$ = data.iterator(); for (; i$.hasNext(); ) { String b = (String)i$.next(); } b = null; i$ = null; }
```

- Declaring the iterator variable outside of the for loop allows a null to be assigned to it as soon as it is no longer used. This makes it accessible to the GC, which can then get rid of the unused memory. Something similar is done for the case when the expression in the enhanced for loop is an array.

P L U S 

- A number of removals.

## [Java 11](https://www.oracle.com/java/technologies/javase/11all-relnotes.html)

### **Most important changes**

**Improved Local Variable Type Inference**

- Introduced in Java SE 10. In this release, it has been enhanced with support for allowing var to be used when declaring the formal parameters of implicitly typed lambda expressions.


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




















