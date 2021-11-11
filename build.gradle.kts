import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.5.10"
    java
}

group = "me.parallels"
version = "1.0-SNAPSHOT"

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

tasks.withType(JavaExec::class) {
    standardInput=System.`in`
}

tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INHERIT }

application {
    mainClass.set("MainKt")
}