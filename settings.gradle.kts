plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "matchmaker"
include("RestAPI")
include("Shared")
include("Wrapper")
include("Demo")
