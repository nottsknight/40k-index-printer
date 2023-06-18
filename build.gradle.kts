plugins {
    kotlin("jvm") version "1.8.20"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "uk.nottsknight"
version = "0.1.0"

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

kotlin {
    jvmToolchain(11)
}

javafx {
    modules("javafx.controls")
}


