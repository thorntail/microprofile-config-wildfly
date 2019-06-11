# MicroProfile Config - WildFly

[WildFly][wildfly]/[Thorntail][thorntail] Extension for [Eclipse MicroProfile Config][microprofile-config], based on the [SmallRye Config][smallrye-config] implementation.

# Instructions

* Compile and install the [Eclipse MicroProfile Config][microprofile-config] project.
* Compile and install this project:

```
mvn clean install
```

# Project structure

* [extension](extension/) - WildFly Extension that provides the `microprofile-config` subsystem. It also allows to define ConfigSources that are stored in the subsystem configuration.
* [feature-pack](feature-pack/) - Feature pack that bundles the extension with the JBoss Modules required to run it in WildFly and Thorntail.
* [dist](dist/) - A distribution of WildFly with the microprofile-config extension installed (in its standalone-microprofile.xml configuration)
* [config-api](config-api/) - Generation of Thorntail Config API that provides a Java API to manage the `microprofile-config` subsystem.

# Links

* [WildFly][wildfly]
* [Thorntail][thorntail]
* [Eclipse MicroProfile Config][microprofile-config]


[wildfly]: https://wildlfy.org/
[thorntail]: https://thorntail.io/
[microprofile-config]: https://github.com/eclipse/microprofile-config/
[smallrye-config]: https://github.com/smallrye/smallrye-config 
