plugins {
    kotlin("jvm") version "2.1.10"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.todolist"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("server.TodoHandler")
}

dependencies {
    testImplementation(kotlin("test"))

    // AWS Lambda Java Core (for Handler)
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")

    // AWS Lambda Java Events (for processing event payloads)
    implementation("com.amazonaws:aws-lambda-java-events:3.11.4")

    // MongoDB Driver
    implementation("org.mongodb:mongodb-driver-sync:4.9.1")

    // MongoDB BSON
    implementation("org.mongodb:bson:4.9.1")

    // JSON Format Support
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}