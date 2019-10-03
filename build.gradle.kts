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