plugins {
    kotlin("jvm") version "1.9.23"
    id("maven-publish")
}

group = "com.readutf.matchmaker"
version = "1.0-SNAPSHOT"


publishing {
    repositories {
        maven {
            name = "readutf"
            url = uri("https://reposilite.readutf.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.readutf.matchmaker"
            artifactId = "api"
            version = "1.0.0"
            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.javalin:javalin:6.1.3")
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.8.0.RC3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
}

repositories {
    maven {
        url = uri("https://reposilite.readutf.org/releases")
    }
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}