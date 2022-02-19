import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    jacoco
}

group = "io.petproject"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val junitVersion = "5.8.2"
    val assertJVersion = "3.22.0"
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        csv.isEnabled = false
        html.isEnabled = false
        xml.isEnabled = true
        xml.destination = File("$buildDir/reports/jacoco/report.xml")
    }
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("io/petproject/utils/*.class")
            }
    )
}

val codeCoverage by tasks.registering {
    group = "verification"
    description = "Gradle tests with Code Coverage"

    dependsOn(tasks.test, tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)

    tasks.findByName("jacocoTestReport")
            ?.mustRunAfter(tasks.findByName("test"))

    tasks.findByName("jacocoTestCoverageVerification")
            ?.mustRunAfter(tasks.findByName("jacocoTestReport"))
}