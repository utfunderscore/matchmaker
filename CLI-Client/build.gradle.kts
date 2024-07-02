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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")


    implementation("com.alibaba:fastjson:2.0.51")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}