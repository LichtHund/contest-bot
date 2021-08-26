import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import dev.triumphteam.helper.*

plugins {
    kotlin("jvm") version "1.5.21"
    id("me.mattstudios.triumph") version "0.2.3"
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
    implementation("net.dv8tion:JDA:4.3.0_307") {
        exclude(module = "opus-java")
    }

    implementation("commons-cli:commons-cli:1.4")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
        }
    }
}