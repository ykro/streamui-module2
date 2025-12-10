package com.curso.android.module2.stream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.curso.android.module2.stream.data.repository.MockMusicRepository
import com.curso.android.module2.stream.ui.navigation.HomeDestination
import com.curso.android.module2.stream.ui.navigation.PlayerDestination
import com.curso.android.module2.stream.ui.navigation.SearchDestination
import com.curso.android.module2.stream.ui.screens.HomeScreen
import com.curso.android.module2.stream.ui.screens.PlayerScreen
import com.curso.android.module2.stream.ui.screens.SearchScreen
import com.curso.android.module2.stream.ui.theme.StreamUITheme
import org.koin.compose.koinInject

/**
 * ================================================================================
 * MAIN ACTIVITY - Punto de Entrada de la UI
 * ================================================================================
 *
 * SINGLE ACTIVITY ARCHITECTURE
 * ----------------------------
 * En apps Compose modernas, típicamente usamos UNA sola Activity.
 * Toda la navegación se maneja internamente con Navigation Compose.
 *
 * Ventajas:
 * - Navegación más fluida (sin recrear Activities)
 * - Estado compartido más fácil
 * - Transiciones personalizables
 * - Mejor integración con Compose
 *
 * COMPONENTES CLAVE:
 * ------------------
 * 1. ComponentActivity: Base moderna para Compose
 * 2. setContent { }: Establece la raíz del árbol de Compose
 * 3. NavHost: Contenedor de destinos de navegación
 * 4. NavController: Controla la navegación (back stack)
 *
 * EDGE TO EDGE:
 * -------------
 * enableEdgeToEdge() hace que la app dibuje detrás de las barras
 * del sistema (status bar, navigation bar). Esto permite UIs
 * más inmersivas con colores personalizados en las barras.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita dibujo edge-to-edge (detrás de barras del sistema)
        enableEdgeToEdge()

        /**
         * setContent { }
         * --------------
         * Establece el contenido de la Activity usando Compose.
         * Todo lo que está dentro es un árbol de Composables.
         *
         * Este es el ÚNICO lugar donde conectamos el mundo tradicional
         * de Android (Activities) con el mundo de Compose.
         */
        setContent {
            StreamUITheme {
                StreamUIApp()
            }
        }
    }
}

/**
 * Composable raíz de la aplicación.
 *
 * Configura:
 * 1. Surface con el color de fondo del tema
 * 2. NavController para manejar navegación
 * 3. NavHost con los destinos de la app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamUIApp() {
    /**
     * rememberNavController()
     * -----------------------
     * Crea y recuerda un NavController.
     *
     * "Remember" significa que sobrevive recomposiciones.
     * El NavController mantiene el back stack de navegación.
     */
    val navController = rememberNavController()

    /**
     * koinInject()
     * ------------
     * Obtiene una dependencia del contenedor de Koin.
     * Aquí inyectamos el repository para buscar canciones por ID.
     *
     * Esto podría estar en un ViewModel dedicado, pero lo hacemos
     * aquí para simplicidad del ejemplo.
     */
    val repository: MockMusicRepository = koinInject()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        /**
         * NAVHOST: Contenedor de Navegación
         * ----------------------------------
         * NavHost define el grafo de navegación de la app.
         *
         * Parámetros:
         * - navController: Controla la navegación
         * - startDestination: Destino inicial (HomeDestination)
         *
         * TYPE-SAFE NAVIGATION (Navigation 2.8+):
         * ---------------------------------------
         * En lugar de strings para las rutas, usamos tipos:
         * - composable<HomeDestination> { } en lugar de composable("home") { }
         * - navController.navigate(PlayerDestination(id)) en lugar de navigate("player/$id")
         *
         * Beneficios:
         * - Errores de tipo en compilación
         * - Autocompletado del IDE
         * - Refactoring seguro
         * - No más errores de typo en rutas
         */
        NavHost(
            navController = navController,
            startDestination = HomeDestination
        ) {
            /**
             * DESTINO: Home Screen
             * --------------------
             * composable<T> define un destino para el tipo T.
             *
             * HomeDestination es un object (sin argumentos),
             * por lo que el lambda no necesita extraer nada.
             */
            composable<HomeDestination> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "StreamUI",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                /**
                                 * ACCIÓN DE BÚSQUEDA
                                 * ------------------
                                 * IconButton en el TopAppBar para navegar a SearchScreen.
                                 *
                                 * Este es un patrón común: acciones en la barra superior
                                 * que llevan a otras pantallas de la app.
                                 */
                                IconButton(
                                    onClick = {
                                        navController.navigate(SearchDestination)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buscar"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                ) { paddingValues ->
                    HomeScreen(
                        onSongClick = { song ->
                            /**
                             * NAVEGACIÓN TYPE-SAFE
                             * --------------------
                             * Navegamos a PlayerDestination pasando el songId.
                             *
                             * El compilador verifica que:
                             * - PlayerDestination existe
                             * - songId es del tipo correcto (String)
                             *
                             * Esto es MUCHO más seguro que:
                             * navController.navigate("player/${song.id}")
                             */
                            navController.navigate(PlayerDestination(songId = song.id))
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            /**
             * DESTINO: Search Screen
             * ----------------------
             * SearchDestination es un object (sin argumentos).
             *
             * Esta pantalla demuestra:
             * 1. Navegación desde Home (icono de búsqueda)
             * 2. Navegación hacia Player (reutiliza PlayerDestination)
             * 3. Navegación hacia atrás (popBackStack)
             *
             * FLUJO DE NAVEGACIÓN:
             * Home ──(search icon)──▶ Search ──(song click)──▶ Player
             *   ◀──────(back)────────   ◀────────(back)────────
             */
            composable<SearchDestination> {
                SearchScreen(
                    onSongClick = { song ->
                        /**
                         * REUTILIZACIÓN DE DESTINOS
                         * -------------------------
                         * Usamos el MISMO PlayerDestination que usa HomeScreen.
                         *
                         * Esto demuestra que los destinos son reutilizables:
                         * - No importa DESDE DÓNDE navegas
                         * - Solo importa A DÓNDE vas y con qué datos
                         *
                         * El back stack maneja automáticamente el regreso:
                         * - Desde Home → Player: back vuelve a Home
                         * - Desde Search → Player: back vuelve a Search
                         */
                        navController.navigate(PlayerDestination(songId = song.id))
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            /**
             * DESTINO: Player Screen
             * ----------------------
             * PlayerDestination es una data class con argumentos.
             *
             * toRoute<T>() extrae los argumentos de forma type-safe:
             * - Deserializa automáticamente los parámetros
             * - Retorna un objeto PlayerDestination completo
             * - El compilador garantiza que el tipo es correcto
             *
             * ANTES (strings, propenso a errores):
             * ```kotlin
             * val songId = backStackEntry.arguments?.getString("songId")
             * // songId podría ser null, typo en "songId", etc.
             * ```
             *
             * AHORA (type-safe):
             * ```kotlin
             * val destination = backStackEntry.toRoute<PlayerDestination>()
             * // destination.songId siempre es válido
             * ```
             */
            composable<PlayerDestination> { backStackEntry ->
                // Extrae los argumentos de navegación de forma type-safe
                val destination = backStackEntry.toRoute<PlayerDestination>()

                // Busca la canción en el repository
                val song = repository.getSongById(destination.songId)

                PlayerScreen(
                    song = song,
                    onBackClick = {
                        /**
                         * popBackStack()
                         * --------------
                         * Navega hacia atrás en el back stack.
                         * Equivalente al botón "back" del sistema.
                         */
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
