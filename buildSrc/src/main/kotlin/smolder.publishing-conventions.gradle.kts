plugins {
    id("smolder.java-conventions")
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        isFailOnError = false
    }
}

var smolderUsername: String? = findProperty("smolderUsername") as String?
var smolderPassword: String? = findProperty("smolderPassword") as String?
val publicRepository: String by project

publishing {
    repositories {
        maven {
            url = uri(publicRepository)
            credentials {
                username = smolderUsername
                password = smolderPassword
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("${project.group}:${project.name}")
                description.set(project.description)
                packaging = "jar"
            }
        }
    }
}
