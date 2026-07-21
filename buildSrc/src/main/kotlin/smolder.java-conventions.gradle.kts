plugins {
    id("java")
    id("java-library")
}

var smolderUsername: String? = System.getenv("REPO_USERNAME")
var smolderPassword: String? = System.getenv("REPO_PASSWORD")
val publicRepository: String by project

repositories {
    mavenLocal()
    mavenCentral()

    maven(publicRepository) {
        name = "smolderPublic"
        credentials {
            username = smolderUsername
            password = smolderPassword
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}