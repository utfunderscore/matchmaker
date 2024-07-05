plugins {
    kotlin("jvm") version "1.9.23"
    id("maven-publish")
    id("java-library")
}

group = "com.readutf.matchmaker"
version = "1.0-SNAPSHOT"

dependencies {
    //add fastjson2
    implementation("com.alibaba:fastjson:2.0.51")

    api("org.panda-lang:expressible:1.3.6") // Core library
    api("org.panda-lang:expressible-kt:1.3.6") // Kotlin extensions
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

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}