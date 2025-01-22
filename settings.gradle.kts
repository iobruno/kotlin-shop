pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
    }

    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "kotlin-shop"