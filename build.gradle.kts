plugins {
    id("java")
    id("maven-publish")

    // use for Java 21 (see: https://github.com/johnrengelman/shadow/pull/876)
    // id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.github.goooler.shadow") version "8.1.8"
    id("io.papermc.paperweight.userdev") version "1.7.1" // Check for new versions at https://plugins.gradle.org/plugin/io.papermc.paperweight.userdev
    id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
}

group = "net.synchthia"
version = "1.21-SNAPSHOT"
description = "Systera"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/groups/public/")
    }

    maven {
        name = "sonatype-oss-repo"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "codemc"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }

    maven {
        name = "dmulloy2"
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        name = "startail-public"
        url = uri("https://maven.pkg.github.com/synchthia/pkg-startail-public")
    }
}

dependencies {
    implementation("net.synchthia:systera-api:1.1-SNAPSHOT")

    // Paper
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

    // Adventure
    implementation("net.kyori:adventure-api:4.17.0")

    // Cloud
    implementation("org.incendo:cloud-core:2.0.0-rc.2")
    implementation("org.incendo:cloud-annotations:2.0.0-rc.2")
    implementation("org.incendo:cloud-paper:2.0.0-beta.8")
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-beta.8")

    // Protocol Buffers
    implementation("com.google.protobuf:protobuf-java:3.21.9")
    implementation("com.google.protobuf:protobuf-java-util:3.21.9")

    // ProtocolLib
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")

    // gRPC
    implementation("io.grpc:grpc-netty:1.64.0")
    implementation("io.grpc:grpc-protobuf:1.64.0")
    implementation("io.grpc:grpc-stub:1.64.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Redis
    implementation("redis.clients:jedis:5.1.3")

    // Utils
    implementation("joda-time:joda-time:2.9.7")
    implementation("commons-configuration:commons-configuration:1.10")

    // gson
    compileOnly("com.google.code.gson:gson:2.11.0")

    // lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks {
    compileJava {
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    shadowJar {
        mergeServiceFiles()
        fun reloc(pkg: String) = relocate(pkg, "net.synchthia.systera.libs.$pkg")

        reloc("redis.clients")
        reloc("org.apache.commons")
        reloc("com.google.protobuf")
        reloc("org.apache.commons.pool2")
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}.jar"))
    }

    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        expand(
            mapOf(
                "pluginName" to "Systera",
                "pluginVersion" to project.version,
                "description" to project.description,
                "authors" to "SYNCHTHIA",
                "website" to "https://synchthia.net",
                "bukkitApiVersion" to "1.20",
                "bukkitPluginMain" to "net.synchthia.systera.SysteraPlugin"
            )
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.reobfJar)
        }
    }
}
