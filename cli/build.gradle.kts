plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.danilopianini.gradle-java-qa") version "1.59.0"
    id("lifecycle-base")
}

group = "it.unibo.ares"
version = "1.0.0"

repositories {
    mavenCentral()
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

dependencies {
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.6")

    val jUnitVersion = "5.10.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    implementation(project(":core"))

}
application {
    mainClass.set("it.unibo.ares.cli.App")
}
