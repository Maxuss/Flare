---
title: Setting up Flare
---

# Setting up Flare

Flare is available on Maven Central. Here are dependency notations for different build tools.
Replace `VERSION` with current latest version (without v):
<br>
![Latest Flare Version](https://img.shields.io/github/v/release/Maxuss/Flare?label=latest%20version)

=== "Maven"

    ```xml
    <dependency>
        <groupId>space.maxus</groupId>
        <artifactId>flare</artifactId>
        <version>VERSION</version>    
    </dependency>
    ```

=== "Gradle (Kotlin)"

    ```kotlin
    implementation("space.maxus:flare:VERSION")
    ```

=== "Gradle (Groovy)"

    ```groovy
    implementation 'space.maxus:flare:VERSION'
    ```

## Include Flare in plugin.yml

Just add Flare as a dependency:

```yaml title="plugin.yml"
depend: [Flare]
```

Currently, Spigot and Bukkit/CraftBukkit are not supported as platforms for Flare. 
You should use Paper or it's forks instead.

After that you are ready to use Flare!