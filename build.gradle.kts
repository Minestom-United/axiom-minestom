 plugins {
    id("smolder.publishing-conventions")
}

dependencies {
    compileOnly("net.minestom:minestom:2025.12.20-1.21.11")
    api("org.slf4j:slf4j-api:2.0.16")
    api("com.github.luben:zstd-jni:1.5.7-4")
}

 java {
     toolchain {
         languageVersion.set(JavaLanguageVersion.of(25)) // Minestom has a minimum Java version of 25
     }
 }