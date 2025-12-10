package com.curso.android.module2.stream.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.curso.android.module2.stream.data.model.Category
import com.curso.android.module2.stream.data.model.Song
import com.curso.android.module2.stream.ui.components.SongCoverMock
import com.curso.android.module2.stream.ui.viewmodel.HomeUiState
import com.curso.android.module2.stream.ui.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * ================================================================================
 * HOME SCREEN - Pantalla Principal
 * ================================================================================
 *
 * Esta pantalla muestra las categorías de música con sus canciones.
 *
 * ESTRUCTURA DE LAYOUTS ANIDADOS:
 * -------------------------------
 *
 *     ┌──────────────────────────────────────────┐
 *     │              LazyColumn                  │ ← Scroll vertical
 *     │  ┌────────────────────────────────────┐  │
 *     │  │  Sección "Rock Classics"           │  │
 *     │  │  ┌────┬────┬────┬────┬────┬───▶   │  │ ← LazyRow (scroll horizontal)
 *     │  │  │ 🎵 │ 🎵 │ 🎵 │ 🎵 │ 🎵 │       │  │
 *     │  │  └────┴────┴────┴────┴────┘       │  │
 *     │  └────────────────────────────────────┘  │
 *     │  ┌────────────────────────────────────┐  │
 *     │  │  Sección "Coding Focus"            │  │
 *     │  │  ┌────┬────┬────┬────┬────┬───▶   │  │
 *     │  │  │ 🎵 │ 🎵 │ 🎵 │ 🎵 │ 🎵 │       │  │
 *     │  │  └────┴────┴────┴────┴────┘       │  │
 *     │  └────────────────────────────────────┘  │
 *     │                   ▼                      │
 *     └──────────────────────────────────────────┘
 *
 * LAZY LAYOUTS:
 * -------------
 * - LazyColumn: Lista vertical con scroll, solo renderiza items visibles
 * - LazyRow: Lista horizontal con scroll, también lazy
 *
 * "Lazy" significa que solo se crean y renderizan los items que están
 * (o están por estar) en pantalla. Esto es CRUCIAL para performance
 * con listas largas.
 *
 * COMPOSICIÓN EN COMPOSE:
 * -----------------------
 * La UI se construye componiendo funciones pequeñas:
 * HomeScreen → CategorySection → SongCard → SongCoverMock
 *
 * Cada componente es reutilizable y testeable independientemente.
 */

/**
 * Pantalla principal que muestra las categorías de música.
 *
 * @param viewModel ViewModel que provee el estado (inyectado por Koin)
 * @param onSongClick Callback cuando el usuario selecciona una canción
 *
 * PATRÓN: State Hoisting
 * ----------------------
 * El callback onSongClick es "elevado" al caller (MainActivity/NavHost).
 * Esto hace que HomeScreen sea:
 * - Más reutilizable (no conoce el destino de navegación)
 * - Más testeable (puedes verificar que el callback se invoca)
 * - Más flexible (el caller decide qué hacer con el click)
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * OBSERVANDO STATEFLOW EN COMPOSE
     * --------------------------------
     * collectAsState() convierte el StateFlow en State de Compose.
     *
     * 'by' es delegación de Kotlin que permite usar uiState directamente
     * en lugar de uiState.value
     *
     * Cuando el StateFlow emite un nuevo valor, este composable
     * se RECOMPONE automáticamente con el nuevo estado.
     */
    val uiState by viewModel.uiState.collectAsState()

    /**
     * RENDERIZADO BASADO EN ESTADO
     * ----------------------------
     * Usamos 'when' para renderizar diferentes UI según el estado.
     * Esto es el corazón del patrón UDF: la UI es una función del estado.
     */
    Box(modifier = modifier) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LoadingContent()
            }

            is HomeUiState.Success -> {
                HomeContent(
                    categories = state.categories,
                    onSongClick = onSongClick
                )
            }

            is HomeUiState.Error -> {
                ErrorContent(message = state.message)
            }
        }
    }
}

/**
 * Contenido de carga (spinner centrado).
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Contenido de error.
 */
@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error
        )
    }
}

/**
 * Contenido principal con las categorías.
 *
 * @param categories Lista de categorías a mostrar
 * @param onSongClick Callback para clicks en canciones
 */
@Composable
private fun HomeContent(
    categories: List<Category>,
    onSongClick: (Song) -> Unit
) {
    /**
     * LAZYCOLUMN: Lista Vertical Eficiente
     * ------------------------------------
     * LazyColumn es el equivalente a RecyclerView en Compose.
     *
     * Características:
     * - Solo compone items visibles (+ buffer)
     * - Recicla composiciones al hacer scroll
     * - Soporta diferentes tipos de items
     *
     * IMPORTANTE: contentPadding añade padding al contenido
     * scrolleable sin afectar el área de scroll.
     */
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        /**
         * items {} es una función de LazyListScope que genera
         * múltiples items a partir de una lista.
         *
         * key = { it.name } proporciona una clave estable para
         * cada item. Esto optimiza recomposiciones cuando la
         * lista cambia (agregados, eliminados, reordenados).
         */
        items(
            items = categories,
            key = { it.name }
        ) { category ->
            CategorySection(
                category = category,
                onSongClick = onSongClick
            )
        }
    }
}

/**
 * Sección de una categoría con título y lista horizontal de canciones.
 *
 * @param category Categoría a mostrar
 * @param onSongClick Callback para clicks en canciones
 */
@Composable
private fun CategorySection(
    category: Category,
    onSongClick: (Song) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Título de la sección
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        /**
         * LAZYROW: Lista Horizontal Eficiente
         * -----------------------------------
         * Similar a LazyColumn pero con scroll horizontal.
         *
         * horizontalArrangement = Arrangement.spacedBy()
         * añade espacio entre items sin necesidad de padding manual.
         *
         * contentPadding permite que los items en los extremos
         * se puedan ver completamente al hacer scroll.
         */
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = category.songs,
                key = { it.id }
            ) { song ->
                SongCard(
                    song = song,
                    onClick = { onSongClick(song) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Tarjeta individual de una canción.
 *
 * @param song Datos de la canción
 * @param onClick Callback cuando se hace click
 */
@Composable
private fun SongCard(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            // clickable hace que toda la columna sea interactiva
            // También añade feedback visual (ripple effect)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cover generado por código
        SongCoverMock(
            colorSeed = song.colorSeed,
            size = 120.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Título de la canción
        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis, // "..." si el texto es muy largo
            modifier = Modifier.fillMaxWidth()
        )

        // Artista
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
