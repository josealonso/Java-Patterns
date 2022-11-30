
As of November 2022, **many teams still use Java 8**, released on 2014.
However, there are two newer Long-Term-Support (LTS) versions: Java 11 and Java 17.

Spring 6.0 and Spring Boot 3.0 were just released. They require Java 17 or a higher version.
So I think it's a good time to **upgrade to Java 17**, which has been around for over a year now, since September 2021. 

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

Before upgrading the JDK, make sure your IDE, build tools, and dependencies are up to date. The Maven Versions Plugin and Gradle Versions Plugin show which dependencies you have and list the latest available version.

Be aware that these tools show only the new version for the artifacts you use—but sometimes the artifact names change, forks are made, or the code moves. For instance, JAXB was first available via javax.xml.bind:jaxb-api but changed to jakarta.xml.bind:jakarta.xml.bind-api after its transition to the Eclipse Foundation. To find such changes, you can use Jonathan Lermitage’s Old GroupIds Alerter plugin for Maven or his plugin for Gradle.

JavaFX. Starting with Java 11, the platform no longer contains JavaFX as part of the specification, and most JDK builds have removed it. You can use the separate JavaFX build from Gluon or add the OpenJFX dependencies to your project.

Fonts. Once upon a time, the JDK contained a few fonts, but as of Java 11 they were removed. If you use, for instance, Apache POI (a Java API for Microsoft Office–compatible documents), you will need fonts. The operating system needs to supply the fonts, since they are no longer present in the JDK. However, on operating systems such as Alpine Linux, the fonts must be installed manually using the apt install fontconfig command. Depending on which fonts you use, extra packages might be required.

Java Mission Control. This is a very useful tool for monitoring and profiling your application. I highly recommend looking into it. Java Mission Control was once included in the JDK, but now it’s available as a separate download under the new name: JDK Mission Control.

Java EE. The biggest change in JDK 11 was the removal of Java EE modules. Java EE modules such as JAXB, mentioned earlier, are used by many applications. You should add the relevant dependencies now that these modules are no longer present in the JDK. Table 1 lists the various modules and their dependencies. Please note that both JAXB and JAX-WS require two dependencies: one for the API and one for the implementation. Another change is the naming convention now that Java EE is maintained by the Eclipse Foundation under the name Jakarta EE. Your package imports need to reflect this change, so for instance jakarta.xml.bind.* should be used instead of javax.xml.bind.*.

