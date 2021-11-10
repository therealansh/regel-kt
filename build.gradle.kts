import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    java
    application
}

group = "me.parallels"
version = "1.0-SNAPSHOT"

ant.importBuild("resnax/build.xml"){ antTargetName ->
    "a-" + antTargetName
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(fileTree("resnax"))
    implementation("com.github.ajalt.clikt:clikt:3.3.0")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
