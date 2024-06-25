plugins {
    kotlin("jvm") version "1.9.23"
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.readutf.matchmaker"
version = "1.0.1"


tasks {
    shadowJar {
        mergeServiceFiles()
    }
}

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
    implementation("io.javalin:javalin:+")
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.8.0.RC3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")
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