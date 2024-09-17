import org.gradle.internal.jvm.Jvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.0.20"
    jacoco
}

group = "io.petproject"
version = "2.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val junitVersion = "5.11.0"
    val kotestVersion = "5.9.1"
    implementation(kotlin("stdlib"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude("io/petproject/utils/*.class")
        }
    )
}
