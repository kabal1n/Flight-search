pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.android") version "2.1.0"
        id("kotlin-kapt") version "2.1.0"
        id("com.android.application") version "8.4.0"
    }

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Flight Search"
include(":app")
