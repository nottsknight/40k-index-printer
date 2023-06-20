plugins {
    kotlin("jvm") version "1.8.20"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("dev.hydraulic.conveyor") version "1.5"
    id("application")
}

group = "uk.nottsknight"
version = "0.1.4"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation("org.apache.pdfbox:pdfbox:2.0.28")
    implementation("no.tornado:tornadofx:1.7.20")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "uk.nottsknight.indexprinter.MainKt"
    }
    configurations["compileClasspath"].forEach { f -> from(zipTree(f.absoluteFile)) }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

javafx {
    modules("javafx.controls")
}

application {
    mainClass.set("uk.nottsknight.indexprinter.MainKt")
}