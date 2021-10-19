import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    application
}

group = "com.s13g.winston"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.3.1")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("elgato-mqtt")
    manifest.attributes["Main-Class"] = "com.s13g.sauron.SauronMainKt"
    manifest.attributes["Implementation-Title"] = "Winston Sauron Daemon"
}

application {
    mainClass.set("MainKt")
}