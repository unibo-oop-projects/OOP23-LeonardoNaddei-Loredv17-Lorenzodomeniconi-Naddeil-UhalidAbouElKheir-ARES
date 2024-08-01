plugins {
    id("java")
    application
}

group = "it.unibo.ares"
version = "1.0.0"

repositories {
    mavenCentral()
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":cli"))
    implementation(project(":gui"))
    implementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}


application {
    mainClass.set("it.unibo.ares.runner.ApplicationRunner")
}
