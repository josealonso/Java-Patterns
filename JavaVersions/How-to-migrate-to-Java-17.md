
As of November 2022, **many teams still use Java 8**, released on 2014.
However, there are two newer Long-Term-Support (LTS) versions: Java 11 and Java 17.

Spring 6.0 and Spring Boot 3.0 were just released. They require Java 17 or a higher version.
So I think it's a good time to **upgrade to Java 17**, which has been around for over a year now, since September 2021. 

**IMPORTANT** The folowing article is a copy from the article called [It’s time to move your applications to Java 17](https://blogs.oracle.com/javamagazine/post/its-time-to-move-your-applications-to-java-17-heres-why-and-heres-how), by [Johan Janssen](https://blogs.oracle.com/authors/johan-janssen)

### **On code migration**

- Upgrading to Java 17 requires effort, especially if the goal is to truly leverage the new language features and functionality within the JVM.

- It might require some effort to upgrade depending on the environment and the application. Developers and other team members need to update their local environment. Then the build environments and runtime environments, such as those for production, require an upgrade as well.

- Fortunately, many projects and organizations use Docker, which helps a lot in this effort. Any serious team should have its own continuous integration/continuous deployment (CI/CD) pipelines, and they run everything in Docker images. Teams can upgrade to the latest Java version by simply specifying that version in their Docker image. This doesn’t impact other teams who might be running on older Java versions, because those teams can use older Docker images.

- The same goes for test and production environments running on Kubernetes. Whenever a team wants to upgrade to a newer Java release, they can change the Docker images themselves and then deploy everything. Of course, if you still have shared build environments, or other teams who manage your environments, the process might be a bit more challenging.

- Estimate how long an upgrade will take is hard to tell, because it mainly depends on how many dependencies your application has. Often, upgrading your dependencies to the latest version resolves many of the issues that would occur during a Java upgrade.

### **What needs to change during a Java upgrade?**

- If something is removed from the JDK, that might break the code, the dependencies, or both. It often helps to make sure those dependencies are up to date to resolve these issues. Sometimes you might have to wait until a framework releases a new version that is compatible with the latest Java version before you begin the upgrade process. This means that you have a good knowledge of the dependencies as part of the preupgrade evaluation process.

- Most functionality isn’t removed all at once from the JDK. First, functionality is marked for deprecation. For instance, Java Architecture for XML Binding (JAXB) was marked for deprecation in Java 9 before being removed in Java 11. If you continuously update, then you see the deprecations and you can resolve any use of those features before the functionality is removed. However, **if you are jumping straight from Java 8 to Java 17, this feature removal will hit you all at once**.

- To view the API changes and, for instance, see which methods are removed or added to the String API in a specific Java version, look at [The Java Version Almanac](https://javaalmanac.io/), by Marc Hoffmann and Cay Horstmann.

### **Multirelease JAR functionality**

- What if your application is used by customers who still use an old JDK and an upgrade at their site is out of your control? Multirelease JAR functionality, introduced in Java 9 with JEP 238, might be useful because it **allows you to package code for multiple Java versions (including versions older than Java 9) inside one JAR file**.

As an example, create an Application class (Listing 1) and a Student class (Listing 2) and place them in the folder src/main/java/com/example. The Student class is a class that runs on Java 8.

```
// Listing 1

public class Application {

   public static void main(String[] args) {
       Student student = new Student("James ");
       System.out.println("Implementation " + student.implementation());
       System.out.println("Student name James contains a blank: " + student.isBlankName());
   }
}
```

```
// Listing 2

public class Student {
   final private String firstName;

   public Student(String firstName) {
       this.firstName = firstName;
   }

   boolean isBlankName() {
       return firstName == null || firstName.trim().isEmpty();
   }

   static String implementation() { return "class"; }
}
```

Next to that, create a Student record (Listing 3) that uses not only **records** (introduced in Java 14) but also the **String.isBlank()** method (introduced in Java 11).

``` 
// Listing 3

public record Student(String firstName) {
   boolean isBlankName() {
       return firstName.isBlank();
   }

   static String implementation() { return "record"; }
}
```

- Some configuration is required depending on the build tool you use. The example is built on Java 17 and creates the JAR file. When the JAR file is executed on JDK 17 or newer, the Student record is used. When the JAR file is executed on older versions, the Student class is used.

- This feature is quite useful, for instance, if new APIs offer better performance, because you can use those APIs for customers who have a recent Java version. The same JAR file can be used for customers with an older JDK, without the performance improvements.

- Please be aware that all the implementations, in this case, the Student, should have the same public API to prevent runtime issues. Unfortunately build tools don’t verify the public APIs, but some IDEs do. Plus, **with JDK 17 you can use the jar –validate command to validate the JAR file**.

- Something to be aware of is the preview functionality present in some versions of the JDK. Some bigger features are first released as **previews** and might result in a **final feature** in one of the next JDKs. Those preview features are present in both LTS and non-LTS versions of Java. The features are enabled with the enable-preview flag and are turned off by default. If you use those preview features in production code, be aware that they might change between JDK versions, which could result in the need for some debugging or refactoring.

### **More about Java deprecations and feature removals**

- Before upgrading the JDK, make sure your IDE, build tools, and dependencies are up to date. The [Maven Versions Plugin](https://www.mojohaus.org/versions-maven-plugin/) and [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) show which dependencies you have and list the latest available version.

- Be aware that these tools show only the new version for the artifacts you use, but sometimes the artifact names change, forks are made, or the code moves. For instance, JAXB was first available via javax.xml.bind:jaxb-api but changed to jakarta.xml.bind:jakarta.xml.bind-api after its transition to the Eclipse Foundation. To find such changes, you can use Jonathan Lermitage’s Old GroupIds Alerter plugin for [Maven](https://github.com/jonathanlermitage/oga-maven-plugin) or his plugin for [Gradle](https://github.com/jonathanlermitage/oga-gradle-plugin).

- **JavaFX.** Starting with Java 11, the platform no longer contains JavaFX as part of the specification, and most JDK builds have removed it. You can use the separate JavaFX build from [Gluon](https://gluonhq.com/products/javafx/) or add the [OpenJFX](https://mvnrepository.com/artifact/org.openjfx) dependencies to your project.

- **Fonts.** If they are needed, the operating system needs to supply the fonts, since they are no longer present in the JDK. 

- **Java Mission Control.** This is a very useful tool for monitoring and profiling your application. Java Mission Control was once included in the JDK, but now it’s available as a separate download under the new name: [JDK Mission Control](https://www.oracle.com/java/technologies/jdk-mission-control.html).

- **Java EE.** The biggest change in JDK 11 was the removal of Java EE modules. Java EE modules such as JAXB are used by many applications. You should add the relevant dependencies now that these modules are no longer present in the JDK. Table 1 lists the various modules and their dependencies. Please note that **both JAXB and JAX-WS require two dependencies: one for the API and one for the implementation.** Another change is the naming convention now that Java EE is maintained by the Eclipse Foundation under the name [Jakarta EE](https://jakarta.ee/). Your package imports need to reflect this change, so for instance **jakarta.xml.bind.* should be used instead of javax.xml.bind.\***.

- **CORBA.** There is no official replacement for Java’s CORBA module, which was removed in Java 11. However, [Oracle GlassFish Server](https://www.oracle.com/middleware/technologies/glassfish-server.html) includes an implementation of CORBA.

- **Nashorn.** Java 15 removed the Nashorn JavaScript engine. You can use the [nashorn-core](https://mvnrepository.com/artifact/org.openjdk.nashorn/nashorn-core) dependency if you still want to use the engine.

- **Experimental compilers.** Java 17 removes support for GraalVM’s experimental ahead-of-time (AOT) and just-in-time (JIT) compiler, as explained in the documentation for [JEP 410](https://openjdk.java.net/jeps/410).

### **Look out for unsupported major files**

- You might see the error `Unsupported class file major version 61`. That happens with the [JaCoCo code coverage library](https://www.eclemma.org/jacoco/) and various other Maven plugins. The major version 61 part of the message refers to Java 17. So in this case, it means that the version of the used framework or tool doesn’t support Java 17. Therefore, you should upgrade the framework or tool to a new version. (If you see a message that contains major version 60, it relates to Java 16.)

### **Encapsulated JDK internal APIs**

- Java 16 and Java 17 encapsulate JDK internal APIs, which impacts various frameworks such as Lombok. You might see errors such as `module jdk.compiler does not export com.sun.tools.javac.processing` to unnamed module, which means your application no longer has access to that part of the JDK.

- It's recommend upgrading all dependencies that use those internals and making sure your own code no longer uses them.

### Java upgrades resource

- The [JavaUpgrades GitHub repository](https://github.com/johanjanssen/javaupgrades) contains examples, common errors, and solutions that can help you during the upgrade process.

### Conclusion

- Upgrading your dependencies and adding dependencies for removed JDK features solves many of the Java upgrade challenges. I recommend a structured approach to upgrading step by step: First, make sure the code compiles, then run your tests, and then run the application.

- In general, **the upgrade from JDK 11 to JDK 17 is a lot easier than the upgrade from JDK 8 to JDK 11.** However, in both scenarios, it was a matter of hours to days for nontrivial applications, and that was mainly due to waiting for builds to complete.












