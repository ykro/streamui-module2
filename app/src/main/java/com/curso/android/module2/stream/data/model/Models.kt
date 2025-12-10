package com.curso.android.module2.stream.data.model

import kotlinx.serialization.Serializable

/**
 * ================================================================================
 * MODELS.KT - Modelos de Datos
 * ================================================================================
 *
 * Este archivo define las entidades principales del dominio de la aplicación.
 *
 * CONCEPTO CLAVE: @Serializable
 * -----------------------------
 * La anotación @Serializable de kotlinx.serialization permite que estas clases
 * sean serializadas/deserializadas automáticamente. Esto es CRUCIAL para:
 *
 * 1. TYPE-SAFE NAVIGATION: Navigation Compose 2.8+ usa serialización para
 *    pasar argumentos entre pantallas de forma segura en tiempo de compilación.
 *
 * 2. Beneficios vs Strings tradicionales:
 *    - Errores de tipo detectados en compilación (no en runtime)
 *    - Autocompletado del IDE
 *    - Refactoring seguro
 *    - No más typos en nombres de argumentos
 *
 * EJEMPLO COMPARATIVO:
 * --------------------
 * ❌ Antiguo (propenso a errores):
 *    navController.navigate("player/123")
 *    val id = backStackEntry.arguments?.getString("songId") // Puede ser null
 *
 * ✅ Nuevo (type-safe):
 *    navController.navigate(PlayerDestination(songId = "123"))
 *    val route = backStackEntry.toRoute<PlayerDestination>() // Tipo garantizado
 */

/**
 * Representa una canción en la aplicación.
 *
 * @property id Identificador único de la canción
 * @property title Título de la canción
 * @property artist Nombre del artista
 * @property colorSeed Semilla para generar el color del cover mock
 *                     (usamos Int en lugar de Color para serialización)
 */
@Serializable
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val colorSeed: Int // Usado para generar gradientes únicos en SongCoverMock
)

/**
 * Representa una categoría o playlist de canciones.
 *
 * @property name Nombre de la categoría (ej: "Rock Classics")
 * @property songs Lista de canciones en esta categoría
 *
 * NOTA: En una app real, 'songs' probablemente sería una lista de IDs
 * y las canciones se cargarían bajo demanda. Aquí usamos objetos
 * completos para simplificar el ejemplo.
 */
@Serializable
data class Category(
    val name: String,
    val songs: List<Song>
)
