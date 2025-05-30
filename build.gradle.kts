import java.util.*

plugins {
    kotlin("jvm") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

val javafxPlatform = System.getProperty("os.name").lowercase(Locale.getDefault()).let {
    when {
        it.contains("win") -> "win"
        it.contains("mac") -> "mac"
        else -> "linux"
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.kwhat:jnativehook:2.2.2")
    implementation("org.apache.commons:commons-compress:1.27.1")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")

    implementation("org.openjfx:javafx-base:21.0.2:$javafxPlatform")
    implementation("org.openjfx:javafx-controls:21.0.2:$javafxPlatform")
    implementation("org.openjfx:javafx-graphics:21.0.2:$javafxPlatform")
}