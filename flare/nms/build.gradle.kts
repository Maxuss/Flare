plugins {
    `java-library`
}

group = "space.maxus"
version = project.properties["core.version"].toString()

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":flare:nms:v1_19_R3"))
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.papermc.paperweight.userdev")

    group = "space.maxus"
    version = project.properties["core.version"].toString()

    dependencies {
        implementation(project(":flare:common"))
    }

    java {
        // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

            // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
            // See https://openjdk.java.net/jeps/247 for more information.
            options.release.set(17)
        }
        javadoc {
            options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        }
        processResources {
            filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        }

        java {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }
}