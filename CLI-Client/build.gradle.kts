plugins {
    kotlin("jvm")
}

group = "com.readutf.matchmaker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":Shared"))
    implementation(project(":Wrapper"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}