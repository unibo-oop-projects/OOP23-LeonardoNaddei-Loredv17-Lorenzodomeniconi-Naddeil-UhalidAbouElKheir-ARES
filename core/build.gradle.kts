plugins {
    java
    application
    base
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.danilopianini.gradle-java-qa") version "1.29.0"
    id("lifecycle-base")
}

group = "it.unibo.ares"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")
    

    val jUnitVersion = "5.10.1"
    // JUnit API and testing engine
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")

}

tasks.test {
    useJUnitPlatform()
}
application {
    // Define the main class for the application
    mainClass.set("it.unibo.ares.core.App")
}