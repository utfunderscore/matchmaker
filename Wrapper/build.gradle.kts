plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
}

group = "com.readutf.matchmaker"
version = "1.0-SNAPSHOT"

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
            artifactId = "Wrapper"
            version = "1.0.0"
            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))

    api(project(":Shared"))

    api("com.neovisionaries:nv-websocket-client:2.14")
    api("com.squareup.retrofit2:retrofit:+")
    api("com.alibaba:fastjson:2.0.51")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    api("com.squareup.okhttp3:logging-interceptor:4.11.0")
    api("io.github.oshai:kotlin-logging-jvm:5.1.0")

    // Logging
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
