val junitVersion: String by project
val assertJVersion: String by project

plugins {
    kotlin("jvm").version("1.3.50")
    jacoco
}

group = "io.petproject"
version = "1.0-SNAPSHOT"

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
    testImplementation("org.assertj:assertj-core:${assertJVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}