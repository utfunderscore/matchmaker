plugins {
    kotlin("jvm") version "1.9.23"
    id("maven-publish")
}

group = "com.readutf.matchmaker"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "readutf-releases"
        url = uri("https://reposilite.readutf.org/releases")
    }
}

publishing {
    repositories {
        maven {
            name = "readutf-releases"
            url = uri("https://reposilite.my-domain.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.readutf.matchmaker"
            artifactId = "shared"
            version = "1.0.0"
            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}