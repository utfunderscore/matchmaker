plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.readutf.matchmaker"
version = "1.0-SNAPSHOT"

subprojects {

    repositories {
        maven {
            url = uri("https://reposilite.readutf.org/releases")
        }
    }
}

repositories {
    maven {
        url = uri("https://reposilite.readutf.org/releases")
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
