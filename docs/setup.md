---
title: Setting up Flare
---

# Setting up Flare

Flare is available on Maven Central. Here are dependency notations for different build tools.
Replace `VERSION` with current latest version (without v):
<br>
![Latest Flare Version](https://img.shields.io/github/v/release/Maxuss/Flare?label=latest%20version)

### Maven

```xml title="pom.xml"
<dependency>
    <groupId>space.maxus</groupId>
    <artifactId>flare</artifactId>
    <version>VERSION</version>    
</dependency>
```

### Gradle (Kotlin)

```kotlin title="build.gradle.kts"
implementation("space.maxus:flare:VERSION")
```

### Gradle (Groovy)

```groovy title="build.gradle"
implementation 'space.maxus:flare:VERSION'
```

## Include Flare in plugin.yml

Since Flare is a library and not a plugin, you should include it in your plugin.yml:

```yaml title="plugin.yml"
libraries:
  - "space.maxus:flare:VERSION"
```

After that you are ready to use Flare!