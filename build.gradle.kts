import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import dev.triumphteam.helper.*

plugins {
    kotlin("jvm") version "1.5.30"
    id("me.mattstudios.triumph") version "0.2.3"
    kotlin("plugin.serialization") version "1.5.30"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "dev.triumphteam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(kotlin("stdlib"))

    // Logger
    implementation("ch.qos.logback:logback-classic:1.2.5")

    // Database stuff
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("org.jetbrains.exposed:exposed-core:0.33.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.33.1")
    implementation("org.xerial:sqlite-jdbc:3.36.0")

    implementation(core(CorePlatform.JDA, "2.0.0"))
    implementation(feature(CoreFeature.CONFIG, "2.0.0"))

    // JDA
    implementation("net.dv8tion:JDA:4.3.0_310") {
        exclude(module = "opus-java")
    }

    implementation("commons-cli:commons-cli:1.4")
    implementation("io.ktor:ktor-client-core:1.6.3")
    implementation("io.ktor:ktor-client-cio:1.6.3")
    implementation("io.ktor:ktor-client-serialization:1.6.3")
    implementation("commons-validator:commons-validator:1.7")

}

application {
    mainClass.set("dev.triumphteam.contest.ApplicationKt")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
        }
    }

    withType<ShadowJar> {
        archiveFileName.set("contest.jar")
    }
}