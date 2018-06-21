# Contentpackage-validation-maven-plugin

Using this package you can validate rules against a content package. At the moment it supports 2 different restrictions

* check if a content package contains files which are not explicitly allowed
* check for the existence of subpackages


Additionally to a pure reporting of collisions with these rules you can also break the build.

## Standalone mode

provide the name of the content package via the "validation.filename" system property.

```
mvn de.joerghoh.maven:contentpackage-validation-maven-plugin:validate -Dvalidation.filename=<path_to_the_contentpackage> "-Dvalidation.whitelistedPaths=!/jcr_root/apps/.*\.jar"  -Dvalidation.breakBuildOnValiationFailures=true
```

This is probably the most often used mode; it allows you to scan a content package for jar bundles (full regular expression support!) and fail if there is one encountered. You can also check for multiple expressions at once:

```
"-Dvalidation.filteredPaths=!/jcr_root/apps.*\.jar,!/jcr_root/apps/.*/config.*/.*\.conf"
```

## Running as part of the build
The plugin can run as part of a maven build; in case any restriction triggers, it will be reported, but the build does not break. If the configuration setting "breakBuild" is set, the build will fail.

Example how to configure:

```
<plugin>
  <groupId>de.joerghoh.maven</groupId>
  <artifactId>contentpackage-validation-maven-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
  <configuration>
    <breakBuild>true</breakBuild>
    <allowSubpackages>true</allowSubpackages>
    <whitelistedPaths>
      <param>!/jcr_root/libs/.*</param>
      <param>!/jcr_root/content/.*</param>
      <param>/META-INF/.*</param>
    </whitelistedPaths>
  </configuration>

</plugin>
```
This configuration will disallow nodes in /libs and /content, but accept the META-INF directory (which is kind of boilderplate and should always be there).

## Building regular expressions
The whitelistedPaths setting allows full regular expressions; you can build positive and negative statements like this:

* positive statements (e.g. ``/jcr_content/apps/myapp/.*``) make sure that certain content is allowed in a package.
* negative statements (e.g. ``!/jcr_content/libs.*``) make sure that any occurrence of paths matching that pattern will be considered as policy violations and reported.


## Supported restrictions
At the moment the plugin supports these restrictions

* path restrictions (system property: validation.whitelistedPaths, maven configuration: whitelistedPaths): A multivalue list of regular expressions, which are allowed. 
* subpackage restrictions (system property: validation.allowSubpackages,  maven configuration: allowSubpackages): either "true" or "false"; if "false" any subpackage will be reported.


## Dumping the content of a package

If you just want to dump the content of a package (including subpackages), use the "dump" goal:

```
mvn de.joerghoh.maven:contentpackage-validation-maven-plugin:dumpContent -Dvalidation.filename=<path_to_the_contentpackage> 
```









