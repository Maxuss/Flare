plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("io.freefair.lombok") version "8.2.2"
    `maven-publish`
    signing

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "space.maxus"
version = project.properties["core.version"].toString()

description = "Reactive Spigot Inventory UI Library"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")

    implementation(project(":flare:common"))
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.typesafe:config:1.4.2")

    runtimeOnly(project(":flare:nms"))

    compileOnly("me.clip:placeholderapi:2.11.3") // PAPI support

    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    jar {
        archiveFileName.set("flare-ui-${project.version}.jar")
    }

    shadowJar {
        relocate("org.bstats", "space.maxus.flare.metrics")
        relocate("com.typesafe", "space.maxus.flare.config")

        archiveFileName.set("flare-ui-${project.version}-all.jar")
    }

    reobfJar {
        base.archivesName.set("flare-ui")
    }

    publish {
        dependsOn(reobfJar)
    }

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
        val props = mapOf(
            "name" to "Flare",
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    signing {
        val signingPassword = providers.environmentVariable("SIGNING_PASSWORD").forUseAtConfigurationTime()
        val signingKey = providers.environmentVariable("SIGNING_KEY").forUseAtConfigurationTime()
//        println("CONFIGURING SIGNING $signingPassword $signingKey")
        if (signingKey.isPresent && signingPassword.isPresent) {
//            println("Using signing key")
//            println(signingKey.get())
//            println(signingPassword.get())
            useInMemoryPgpKeys(signingKey.get(), signingPassword.get())
            sign(publishing.publications)
        }
    }

    publishing {
        repositories {
            maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "ossrh"
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
            maven("https://maven.pkg.github.com/Maxuss/Flare") {
                name = "github"
                credentials {
                    username = "Maxuss"
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }

        publications {
            register<MavenPublication>(project.name) {
                from(getComponents()["java"])
                artifact(getTasks().shadowJar.get().outputs.files.single())

                groupId = project.group.toString()
                artifactId = "flare"
                version = project.version.toString()

                pom {
                    name.set("flare")
                    description.set(project.description)
                    url.set("https://github.com/Maxuss/Flare")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://opensource.org/licenses/Apache-2.0")
                        }
                    }
                    developers {
                        developer {
                            name.set("maxuss")
                        }
                    }
                    scm {
                        url.set(
                            "https://github.com/Maxuss/Flare.git"
                        )
                        connection.set(
                            "scm:git:git://github.com/Maxuss/Flare.git"
                        )
                        developerConnection.set(
                            "scm:git:git://github.com/Maxuss/Flare.git"
                        )
                    }
                    issueManagement {
                        url.set("https://github.com/Maxuss/Flare/issues")
                    }
                }
            }
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    named("generateMetadataFileForCorePublication") {
        dependsOn(reobfJar)
    }
}
