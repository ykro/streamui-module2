/**
 * ================================================================================
 * BUILD.GRADLE.KTS (PROJECT LEVEL)
 * ================================================================================
 *
 * Este archivo configura los plugins a nivel de proyecto.
 * En Gradle moderno, usamos el bloque 'plugins' con 'apply false' para declarar
 * los plugins que serán aplicados en los módulos (como :app).
 *
 * CONCEPTO CLAVE: Version Catalogs
 * ---------------------------------
 * Los alias como 'libs.plugins.android.application' vienen definidos en
 * gradle/libs.versions.toml. Esto centraliza las versiones y facilita
 * el mantenimiento del proyecto.
 */
plugins {
    // Plugin de Android Application - se aplica en el módulo :app
    alias(libs.plugins.android.application) apply false

    // Plugin de Kotlin para Android
    alias(libs.plugins.kotlin.android) apply false

    // Plugin de Compose Compiler (requerido desde Kotlin 2.0)
    alias(libs.plugins.kotlin.compose) apply false

    // Plugin de Serialización - necesario para @Serializable en Type-Safe Navigation
    alias(libs.plugins.kotlin.serialization) apply false
}
