plugins {
    id("smolder.publishing-conventions")
}

repositories {
    maven(url = "https://central.sonatype.com/repository/maven-snapshots/") {
        content {
            includeModule("net.minestom", "minestom")
        }
    }
    mavenCentral()
}

dependencies {
    compileOnly("net.minestom:minestom:26_1-SNAPSHOT")
    api("org.slf4j:slf4j-api:2.0.16")
    api("com.github.luben:zstd-jni:1.5.7-4")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25)) // Minestom has a minimum Java version of 25
    }
}