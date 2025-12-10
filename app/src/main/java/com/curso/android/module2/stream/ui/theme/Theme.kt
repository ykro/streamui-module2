package com.curso.android.module2.stream.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * ================================================================================
 * THEME.KT - Sistema de Temas de la Aplicación
 * ================================================================================
 *
 * MATERIAL THEME EN COMPOSE
 * -------------------------
 * MaterialTheme es el sistema de diseño de Material Design 3 en Compose.
 * Proporciona un sistema consistente de:
 * - Colores (colorScheme)
 * - Tipografía (typography)
 * - Formas (shapes)
 *
 * DYNAMIC COLOR (Android 12+)
 * ---------------------------
 * En Android 12 y superior, los colores pueden derivarse del wallpaper
 * del usuario (Material You). Esto crea una experiencia personalizada.
 *
 * dynamicDarkColorScheme() / dynamicLightColorScheme()
 * - Genera un esquema de colores basado en el wallpaper
 * - Solo funciona en Android 12+ (API 31+)
 * - En versiones anteriores, usamos colores estáticos
 */

// ==========================================
// COLORES PERSONALIZADOS
// ==========================================

// Colores primarios (para botones, elementos destacados)
private val Purple40 = Color(0xFF6650a4)
private val PurpleGrey40 = Color(0xFF625b71)
private val Pink40 = Color(0xFF7D5260)

private val Purple80 = Color(0xFFD0BCFF)
private val PurpleGrey80 = Color(0xFFCCC2DC)
private val Pink80 = Color(0xFFEFB8C8)

// Colores para música/streaming
private val StreamPrimary = Color(0xFF1DB954)       // Verde Spotify-like
private val StreamPrimaryDark = Color(0xFF1ED760)
private val StreamSecondary = Color(0xFF191414)     // Negro profundo
private val StreamSurface = Color(0xFF121212)        // Gris muy oscuro
private val StreamSurfaceLight = Color(0xFFFAFAFA)   // Blanco suave

/**
 * Esquema de colores para modo oscuro.
 *
 * En apps de música, el modo oscuro es típicamente el predeterminado
 * ya que reduce fatiga visual durante uso prolongado.
 */
private val DarkColorScheme = darkColorScheme(
    primary = StreamPrimary,
    onPrimary = Color.Black,
    primaryContainer = StreamPrimary.copy(alpha = 0.3f),
    onPrimaryContainer = StreamPrimaryDark,

    secondary = PurpleGrey80,
    onSecondary = Color.Black,
    secondaryContainer = PurpleGrey40,
    onSecondaryContainer = PurpleGrey80,

    tertiary = Pink80,
    onTertiary = Color.Black,

    background = StreamSurface,
    onBackground = Color.White,

    surface = StreamSecondary,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB3B3B3)
)

/**
 * Esquema de colores para modo claro.
 */
private val LightColorScheme = lightColorScheme(
    primary = StreamPrimary,
    onPrimary = Color.White,
    primaryContainer = StreamPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFF0D5F2C),

    secondary = PurpleGrey40,
    onSecondary = Color.White,
    secondaryContainer = PurpleGrey80,
    onSecondaryContainer = PurpleGrey40,

    tertiary = Pink40,
    onTertiary = Color.White,

    background = StreamSurfaceLight,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666)
)

/**
 * Tema principal de StreamUI.
 *
 * @param darkTheme Si usar modo oscuro (por defecto sigue la configuración del sistema)
 * @param dynamicColor Si usar colores dinámicos del wallpaper (solo Android 12+)
 * @param content Contenido de la app
 *
 * USO:
 * ```kotlin
 * StreamUITheme {
 *     // Tu contenido aquí
 *     Surface { ... }
 * }
 * ```
 *
 * TODOS los composables dentro de StreamUITheme pueden acceder a:
 * - MaterialTheme.colorScheme
 * - MaterialTheme.typography
 * - MaterialTheme.shapes
 */
@Composable
fun StreamUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color está disponible en Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /**
     * SELECCIÓN DE COLOR SCHEME
     * -------------------------
     * 1. Si dynamicColor está habilitado Y estamos en Android 12+:
     *    Usa colores del wallpaper
     * 2. Si no:
     *    Usa esquema estático (DarkColorScheme o LightColorScheme)
     */
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    /**
     * MaterialTheme
     * -------------
     * Proporciona el tema a todo el árbol de composables.
     *
     * Cualquier composable hijo puede acceder a los valores del tema:
     * - MaterialTheme.colorScheme.primary
     * - MaterialTheme.typography.headlineMedium
     * - MaterialTheme.shapes.medium
     */
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
