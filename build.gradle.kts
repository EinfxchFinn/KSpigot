import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val githubRepo = "jakobkmar/KSpigot"

group = "net.axay"
version = "1.18.0"

description = "A Kotlin API for Minecraft plugins using the Spigot or Paper toolchain"

plugins {
    kotlin("jvm") version "1.6.0"

    `java-library`
    `maven-publish`
    signing

    id("org.jetbrains.dokka") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.0"

    id("io.papermc.paperweight.userdev") version "1.3.1"
}

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0-RC2")
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    dokkaHtml.configure {
        outputDirectory.set(projectDir.resolve("docs"))
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

signing {
    sign(publishing.publications)
}

publishing {
    repositories {
        maven("https://oss.sonatype.org/service/local/staging/deploy/maven2") {
            name = "ossrh"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.reobfJar)
            artifact(tasks.named("javadocJar"))
            artifact(tasks.named("sourcesJar"))

            this.groupId = project.group.toString()
            this.artifactId = project.name.toLowerCase()
            this.version = project.version.toString()

            pom {
                name.set(project.name)
                description.set(project.description)

                developers {
                    developer {
                        name.set("jakobkmar")
                    }
                }

                licenses {
                    license {
                        name.set("GNU General Public License, Version 3")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }

                url.set("https://github.com/${githubRepo}")

                scm {
                    connection.set("scm:git:git://github.com/${githubRepo}.git")
                    url.set("https://github.com/${githubRepo}/tree/main")
                }
            }
        }
    }
}
