package com.curso.android.module2.stream.ui.navigation

import kotlinx.serialization.Serializable

/**
 * ================================================================================
 * DESTINATIONS.KT - Definición de Rutas Type-Safe
 * ================================================================================
 *
 * TYPE-SAFE NAVIGATION (Navigation Compose 2.8+)
 * -----------------------------------------------
 * A partir de Navigation Compose 2.8.0, podemos definir rutas de navegación
 * usando clases y objetos Kotlin marcados con @Serializable, en lugar de
 * strings propensos a errores.
 *
 * ANTES (Navigation < 2.8):
 * ```kotlin
 * // Definición de rutas como strings
 * const val HOME_ROUTE = "home"
 * const val PLAYER_ROUTE = "player/{songId}"
 *
 * // Navegación propensa a errores
 * navController.navigate("player/$songId") // Typo posible
 * val songId = backStackEntry.arguments?.getString("songId") // Puede ser null
 * ```
 *
 * AHORA (Type-Safe Navigation):
 * ```kotlin
 * @Serializable object HomeDestination
 * @Serializable data class PlayerDestination(val songId: String)
 *
 * // Navegación type-safe
 * navController.navigate(PlayerDestination(songId = "123"))
 * val route = backStackEntry.toRoute<PlayerDestination>() // Tipo garantizado
 * ```
 *
 * ================================================================================
 * REGLAS PARA DEFINIR DESTINOS
 * ================================================================================
 *
 * 1. OBJECT (@Serializable object):
 *    - Usar para destinos SIN argumentos
 *    - Ejemplo: HomeDestination (pantalla principal sin parámetros)
 *
 * 2. DATA CLASS (@Serializable data class):
 *    - Usar para destinos CON argumentos
 *    - Los parámetros del constructor son los argumentos de navegación
 *    - Ejemplo: PlayerDestination(songId: String)
 *
 * 3. TIPOS SOPORTADOS para argumentos:
 *    - Primitivos: String, Int, Long, Float, Boolean
 *    - Enums
 *    - Tipos serializables personalizados
 *    - Nullable types (con ?)
 *    - Listas de los anteriores
 */

/**
 * Destino: Pantalla Principal (Home)
 *
 * Usamos 'data object' (Kotlin 1.9+) en lugar de 'object' para mejor
 * compatibilidad con serialización y debugging.
 *
 * Esta pantalla no requiere argumentos, por lo que es un object simple.
 */
@Serializable
data object HomeDestination

/**
 * Destino: Pantalla de Búsqueda
 *
 * Similar a HomeDestination, no requiere argumentos.
 * Es un punto de entrada independiente donde el usuario puede buscar canciones.
 *
 * NAVEGACIÓN MULTI-NIVEL:
 * ----------------------
 * Esta pantalla demuestra navegación desde múltiples orígenes:
 * - Home → Search (usuario quiere buscar)
 * - Search → Player (usuario encontró una canción)
 *
 * El mismo PlayerDestination se usa tanto desde Home como desde Search,
 * demostrando la reutilización de rutas.
 */
@Serializable
data object SearchDestination

/**
 * Destino: Pantalla del Reproductor
 *
 * @property songId ID de la canción a reproducir
 *
 * Usamos data class porque necesitamos pasar el ID de la canción.
 * Navigation Compose automáticamente:
 * 1. Serializa songId al navegar: navController.navigate(PlayerDestination("rock_1"))
 * 2. Deserializa al llegar: backStackEntry.toRoute<PlayerDestination>().songId
 *
 * ALTERNATIVA: Pasar el objeto Song completo
 * ------------------------------------------
 * Podrías pasar todo el objeto Song si es pequeño:
 *
 * @Serializable
 * data class PlayerDestination(val song: Song)
 *
 * Sin embargo, es mejor práctica pasar solo el ID y cargar los datos
 * en el destino. Esto porque:
 * - URLs más cortas y limpias
 * - Deep links más fáciles de implementar
 * - Single source of truth (los datos vienen del repository)
 */
@Serializable
data class PlayerDestination(
    val songId: String
)
