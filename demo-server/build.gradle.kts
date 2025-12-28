plugins {
    id("java")
}

group = "fr.ghostrider584"
version = "0.0.3"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation("net.minestom:minestom:2025.12.20-1.21.11")
    implementation("ch.qos.logback:logback-classic:1.5.16")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25)) // Minestom has a minimum Java version of 25
    }
}