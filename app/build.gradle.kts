/**
 * ================================================================================
 * BUILD.GRADLE.KTS (APP MODULE)
 * ================================================================================
 *
 * Configuración del módulo principal de la aplicación.
 *
 * CONCEPTOS CLAVE:
 * ----------------
 * 1. PLUGINS: Extienden la funcionalidad de Gradle
 *    - android.application: Configura el proyecto como app Android
 *    - kotlin.android: Soporte de Kotlin para Android
 *    - kotlin.compose: Compiler plugin para Jetpack Compose
 *    - kotlin.serialization: Genera código para @Serializable
 *
 * 2. VERSION CATALOGS: Las dependencias usan 'libs.xxx' definidas en
 *    gradle/libs.versions.toml
 *
 * 3. BOM (Bill of Materials): Gestiona versiones compatibles de múltiples
 *    librerías relacionadas (Compose BOM, Koin BOM)
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // ⚠️ IMPORTANTE: Este plugin es REQUERIDO para Type-Safe Navigation
    // Permite usar @Serializable en las clases de ruta
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.curso.android.module2.stream"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.curso.android.module2.stream"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    /**
     * COMPILER OPTIONS
     * -----------------
     * Configuración moderna usando compilerOptions (reemplaza kotlinOptions)
     * jvmTarget debe coincidir con sourceCompatibility/targetCompatibility
     */
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ==========================================
    // ANDROIDX CORE
    // ==========================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ==========================================
    // JETPACK COMPOSE (usando BOM)
    // ==========================================
    /**
     * BOM (Bill of Materials)
     * -----------------------
     * platform() importa el BOM que define versiones compatibles
     * para TODAS las librerías de Compose. Las dependencias debajo
     * NO necesitan especificar versión.
     */
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // Íconos extendidos (MusicNote, PlayArrow, etc.)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ==========================================
    // NAVIGATION COMPOSE
    // ==========================================
    /**
     * Navigation Compose 2.8.0+ soporta Type-Safe Navigation
     * Permite definir rutas como @Serializable data class/object
     * en lugar de Strings propensos a errores
     */
    implementation(libs.androidx.navigation.compose)

    // ==========================================
    // KOIN - Inyección de Dependencias (usando BOM)
    // ==========================================
    /**
     * KOIN: Framework ligero de DI para Kotlin
     * ----------------------------------------
     * - koin-android: Core de Koin + integración Android
     * - koin-androidx-compose: Función koinViewModel() para Compose
     *
     * ¿Por qué DI (Dependency Injection)?
     * - Desacopla la creación de objetos de su uso
     * - Facilita testing (puedes inyectar mocks)
     * - Centraliza la configuración de dependencias
     */
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // ==========================================
    // KOTLINX SERIALIZATION
    // ==========================================
    /**
     * Requerido para Type-Safe Navigation
     * Permite serializar/deserializar argumentos de navegación
     * automáticamente al pasar datos entre pantallas
     */
    implementation(libs.kotlinx.serialization.json)
}
