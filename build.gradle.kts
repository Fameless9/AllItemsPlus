plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.1"
    id("com.diffplug.spotless") version "7.0.0.BETA1"
}

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    compileOnly(libs.org.spigotmc.spigot.api)
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.17.0")
}

group = "net.fameless"
version = "1.0.1"
description = "AllItemsPlus"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    shadowJar {
        this.archiveClassifier.set(null as String?)
        this.archiveFileName.set("${project.name}-${project.version}.${this.archiveExtension.getOrElse("jar")}")

        relocate("org.bstats", "net.fameless.relocation.bstats")
    }

    build {
        dependsOn(shadowJar)
    }


    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }

    spotless {
        java {
            target("**/*.java")
            removeUnusedImports()
            toggleOffOn()
            trimTrailingWhitespace()
            endWithNewline()
            formatAnnotations()
            indentWithSpaces(4)
        }
    }
}
