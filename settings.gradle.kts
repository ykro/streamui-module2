/**
 * ================================================================================
 * SETTINGS.GRADLE.KTS
 * ================================================================================
 *
 * Configuración del proyecto Gradle. Define:
 * 1. Repositorios para resolver plugins
 * 2. Repositorios para resolver dependencias
 * 3. Módulos incluidos en el proyecto
 */

pluginManagement {
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

rootProject.name = "StreamUI"
include(":app")
