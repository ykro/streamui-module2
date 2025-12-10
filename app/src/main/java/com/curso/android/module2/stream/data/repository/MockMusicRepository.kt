package com.curso.android.module2.stream.data.repository

import com.curso.android.module2.stream.data.model.Category
import com.curso.android.module2.stream.data.model.Song

/**
 * ================================================================================
 * MOCK MUSIC REPOSITORY
 * ================================================================================
 *
 * Repositorio que proporciona datos simulados para la aplicación.
 *
 * PATRÓN REPOSITORY
 * -----------------
 * El patrón Repository abstrae el origen de los datos de la lógica de negocio.
 * La UI y los ViewModels no saben (ni les importa) de dónde vienen los datos:
 * - Base de datos local (Room)
 * - API remota (Retrofit)
 * - Datos mock (este caso)
 *
 * Beneficios:
 * 1. Fácil testing: Inyecta un mock repository en tests
 * 2. Cambio de fuente de datos sin modificar ViewModels
 * 3. Single source of truth para los datos
 *
 * INTERFACE vs CLASS
 * ------------------
 * En una app real, definiríamos una interface MusicRepository y múltiples
 * implementaciones (MockMusicRepository, RemoteMusicRepository, etc.).
 * Para este ejemplo educativo, usamos una clase directa.
 */
class MockMusicRepository {

    /**
     * Obtiene todas las categorías con sus canciones.
     *
     * En una app real, esto sería una función suspend que haría una
     * llamada a red o base de datos. Aquí retornamos datos estáticos.
     */
    fun getCategories(): List<Category> = categories

    /**
     * Busca una canción por su ID.
     *
     * @param songId ID de la canción a buscar
     * @return La canción encontrada o null si no existe
     *
     * Nota: flatMap aplana la lista de listas de canciones en una sola lista
     */
    fun getSongById(songId: String): Song? {
        return categories
            .flatMap { it.songs }
            .find { it.id == songId }
    }

    /**
     * Obtiene todas las canciones de todas las categorías.
     *
     * @return Lista plana de todas las canciones disponibles
     *
     * Útil para búsquedas globales donde no importa la categoría.
     * flatMap convierte List<Category> → List<Song> aplanando las listas anidadas.
     */
    fun getAllSongs(): List<Song> {
        return categories.flatMap { it.songs }
    }

    companion object {
        /**
         * Datos mock de la aplicación.
         *
         * colorSeed: Valores diferentes generan gradientes únicos.
         * Usamos valores espaciados para maximizar la variedad visual.
         */
        private val categories = listOf(
            // ==========================================
            // CATEGORÍA 1: Rock Classics
            // ==========================================
            Category(
                name = "Rock Classics",
                songs = listOf(
                    Song("rock_1", "Highway to Hell", "AC/DC", 0xFF1E88E5.toInt()),
                    Song("rock_2", "Bohemian Rhapsody", "Queen", 0xFF8E24AA.toInt()),
                    Song("rock_3", "Stairway to Heaven", "Led Zeppelin", 0xFF43A047.toInt()),
                    Song("rock_4", "Sweet Child O' Mine", "Guns N' Roses", 0xFFE53935.toInt()),
                    Song("rock_5", "Back in Black", "AC/DC", 0xFF3949AB.toInt()),
                    Song("rock_6", "Hotel California", "Eagles", 0xFFFB8C00.toInt()),
                    Song("rock_7", "Comfortably Numb", "Pink Floyd", 0xFF00ACC1.toInt()),
                    Song("rock_8", "November Rain", "Guns N' Roses", 0xFF7CB342.toInt()),
                    Song("rock_9", "Dream On", "Aerosmith", 0xFFD81B60.toInt()),
                    Song("rock_10", "Smoke on the Water", "Deep Purple", 0xFF5E35B1.toInt())
                )
            ),

            // ==========================================
            // CATEGORÍA 2: Coding Focus
            // ==========================================
            Category(
                name = "Coding Focus",
                songs = listOf(
                    Song("code_1", "Weightless", "Marconi Union", 0xFF26A69A.toInt()),
                    Song("code_2", "Clair de Lune", "Debussy", 0xFF5C6BC0.toInt()),
                    Song("code_3", "Experience", "Ludovico Einaudi", 0xFF66BB6A.toInt()),
                    Song("code_4", "Time", "Hans Zimmer", 0xFF42A5F5.toInt()),
                    Song("code_5", "Gymnopédie No.1", "Erik Satie", 0xFFAB47BC.toInt()),
                    Song("code_6", "River Flows in You", "Yiruma", 0xFF26C6DA.toInt()),
                    Song("code_7", "Nuvole Bianche", "Ludovico Einaudi", 0xFF9CCC65.toInt()),
                    Song("code_8", "Interstellar Main Theme", "Hans Zimmer", 0xFF7E57C2.toInt()),
                    Song("code_9", "Arrival of the Birds", "The Cinematic Orchestra", 0xFFFFCA28.toInt()),
                    Song("code_10", "On the Nature of Daylight", "Max Richter", 0xFFEF5350.toInt())
                )
            ),

            // ==========================================
            // CATEGORÍA 3: Gym Energy
            // ==========================================
            Category(
                name = "Gym Energy",
                songs = listOf(
                    Song("gym_1", "Stronger", "Kanye West", 0xFFFF7043.toInt()),
                    Song("gym_2", "Lose Yourself", "Eminem", 0xFF78909C.toInt()),
                    Song("gym_3", "Eye of the Tiger", "Survivor", 0xFFFFA726.toInt()),
                    Song("gym_4", "Till I Collapse", "Eminem", 0xFF5C6BC0.toInt()),
                    Song("gym_5", "Can't Hold Us", "Macklemore", 0xFF66BB6A.toInt()),
                    Song("gym_6", "Power", "Kanye West", 0xFFEC407A.toInt()),
                    Song("gym_7", "Remember the Name", "Fort Minor", 0xFF29B6F6.toInt()),
                    Song("gym_8", "Thunderstruck", "AC/DC", 0xFFFFEE58.toInt()),
                    Song("gym_9", "Believer", "Imagine Dragons", 0xFFAB47BC.toInt()),
                    Song("gym_10", "Warriors", "Imagine Dragons", 0xFF26A69A.toInt())
                )
            ),

            // ==========================================
            // CATEGORÍA 4: Chill Vibes
            // ==========================================
            Category(
                name = "Chill Vibes",
                songs = listOf(
                    Song("chill_1", "Sunset Lover", "Petit Biscuit", 0xFFFFAB91.toInt()),
                    Song("chill_2", "Intro", "The xx", 0xFF90A4AE.toInt()),
                    Song("chill_3", "Midnight City", "M83", 0xFFCE93D8.toInt()),
                    Song("chill_4", "Electric Feel", "MGMT", 0xFF80DEEA.toInt()),
                    Song("chill_5", "Breathe", "Télépopmusik", 0xFFA5D6A7.toInt()),
                    Song("chill_6", "Teardrop", "Massive Attack", 0xFFB39DDB.toInt()),
                    Song("chill_7", "Porcelain", "Moby", 0xFF81D4FA.toInt()),
                    Song("chill_8", "Fade Into You", "Mazzy Star", 0xFFF48FB1.toInt()),
                    Song("chill_9", "Skinny Love", "Bon Iver", 0xFFFFCC80.toInt()),
                    Song("chill_10", "Holocene", "Bon Iver", 0xFFC5E1A5.toInt())
                )
            ),

            // ==========================================
            // CATEGORÍA 5: Latin Hits
            // ==========================================
            Category(
                name = "Latin Hits",
                songs = listOf(
                    Song("latin_1", "Despacito", "Luis Fonsi", 0xFFFF8A65.toInt()),
                    Song("latin_2", "Bailando", "Enrique Iglesias", 0xFFFFD54F.toInt()),
                    Song("latin_3", "La Bicicleta", "Shakira & Carlos Vives", 0xFF4DD0E1.toInt()),
                    Song("latin_4", "Vivir Mi Vida", "Marc Anthony", 0xFFAED581.toInt()),
                    Song("latin_5", "Danza Kuduro", "Don Omar", 0xFFBA68C8.toInt()),
                    Song("latin_6", "Livin' la Vida Loca", "Ricky Martin", 0xFFFF8A80.toInt()),
                    Song("latin_7", "Waka Waka", "Shakira", 0xFF82B1FF.toInt()),
                    Song("latin_8", "Súbeme la Radio", "Enrique Iglesias", 0xFFB9F6CA.toInt()),
                    Song("latin_9", "Mi Gente", "J Balvin", 0xFFFFE57F.toInt()),
                    Song("latin_10", "Gasolina", "Daddy Yankee", 0xFFEA80FC.toInt())
                )
            )
        )
    }
}
