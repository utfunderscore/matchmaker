plugins {
    kotlin("jvm")
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
            artifactId = "wrapper"
            version = "1.0.0"
            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":Shared"))

    implementation("com.tinder.scarlet:scarlet:0.1.12")
    implementation("com.squareup.retrofit2:retrofit:+")
    implementation("com.alibaba:fastjson:2.0.51")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}