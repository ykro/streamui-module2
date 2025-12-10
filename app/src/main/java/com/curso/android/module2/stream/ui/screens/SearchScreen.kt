package com.curso.android.module2.stream.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.curso.android.module2.stream.data.model.Song
import com.curso.android.module2.stream.ui.components.SongCoverMock
import com.curso.android.module2.stream.ui.viewmodel.SearchViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * ================================================================================
 * SEARCH SCREEN - Pantalla de Búsqueda
 * ================================================================================
 *
 * Esta pantalla demuestra conceptos adicionales de Compose y MVVM:
 *
 * 1. MANEJO DE INPUT DEL USUARIO:
 *    - TextField controlado por estado del ViewModel
 *    - Eventos onValueChange que actualizan el estado
 *
 * 2. NAVEGACIÓN DESDE TERCERA PANTALLA:
 *    - Home → Search → Player (demuestra navegación multi-nivel)
 *    - Misma ruta PlayerDestination usada desde diferente origen
 *
 * 3. COMPONENTES REUTILIZADOS:
 *    - SongCoverMock usado igual que en HomeScreen
 *    - Mismo patrón de callbacks para navegación
 *
 * ESTRUCTURA DE LA PANTALLA:
 * --------------------------
 *
 *     ┌────────────────────────────────────┐
 *     │  ← |  🔍 Buscar canciones...   X  │ ← TopAppBar con TextField
 *     ├────────────────────────────────────┤
 *     │                                    │
 *     │  ┌────┐ Highway to Hell           │
 *     │  │ 🎵 │ AC/DC                     │ ← LazyColumn
 *     │  └────┘                           │   (lista vertical)
 *     │  ┌────┐ Back in Black             │
 *     │  │ 🎵 │ AC/DC                     │
 *     │  └────┘                           │
 *     │         ...                        │
 *     │                                    │
 *     └────────────────────────────────────┘
 *
 * A diferencia de HomeScreen (LazyColumn + LazyRow), aquí usamos
 * solo LazyColumn porque mostramos resultados en lista simple.
 */

/**
 * Pantalla de búsqueda de canciones.
 *
 * @param viewModel ViewModel que maneja la lógica de búsqueda (inyectado por Koin)
 * @param onSongClick Callback cuando el usuario selecciona una canción
 * @param onBackClick Callback para el botón de retroceso
 *
 * PATRÓN: State Hoisting (elevación de estado)
 * -------------------------------------------
 * Los callbacks onSongClick y onBackClick son "elevados" al caller.
 * Esta pantalla NO conoce:
 * - A dónde navegar cuando se hace click en una canción
 * - Qué hacer cuando se presiona "atrás"
 *
 * El NavHost en MainActivity define estos comportamientos.
 * Esto hace que SearchScreen sea reutilizable y testeable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onSongClick: (Song) -> Unit,
    onBackClick: () -> Unit
) {
    /**
     * OBSERVANDO EL ESTADO
     * --------------------
     * Igual que en HomeScreen, observamos el StateFlow del ViewModel.
     * Cada vez que el usuario escribe, el estado cambia y la UI se recompone.
     */
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.query,
                onQueryChange = { viewModel.updateQuery(it) },
                onClearClick = { viewModel.clearSearch() },
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        SearchContent(
            songs = uiState.displayedSongs,
            isSearching = uiState.isSearching,
            query = uiState.query,
            onSongClick = onSongClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * TopAppBar con campo de búsqueda integrado.
 *
 * @param query Texto actual de búsqueda
 * @param onQueryChange Callback cuando el texto cambia
 * @param onClearClick Callback para limpiar la búsqueda
 * @param onBackClick Callback para retroceder
 *
 * TEXTFIELD CONTROLADO:
 * --------------------
 * Un TextField "controlado" significa que su valor viene del estado externo
 * (en este caso, del ViewModel via uiState.query).
 *
 * ```kotlin
 * TextField(
 *     value = query,              // El estado controla qué se muestra
 *     onValueChange = { ... }     // Usuario escribe → actualiza estado → TextField se actualiza
 * )
 * ```
 *
 * Este patrón es fundamental en Compose y en UDF:
 * - La UI REFLEJA el estado (no lo contiene)
 * - Los cambios SIEMPRE pasan por el ViewModel
 * - Single source of truth
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            /**
             * TEXTFIELD EN TOPAPPBAR
             * ----------------------
             * Colocamos el TextField en el slot "title" del TopAppBar.
             * Esto crea una experiencia de búsqueda integrada.
             *
             * TextFieldDefaults.colors() personaliza los colores para
             * que el TextField se integre visualmente con el TopAppBar.
             */
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Buscar canciones o artistas...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    // Solo mostrar el botón X si hay texto
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearClick) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    // Fondo transparente para integrarse con TopAppBar
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    // Sin indicador inferior (la línea debajo del TextField)
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

/**
 * Contenido principal de la pantalla de búsqueda.
 *
 * @param songs Lista de canciones a mostrar
 * @param isSearching Si el usuario está buscando activamente
 * @param query Texto de búsqueda actual
 * @param onSongClick Callback para click en canción
 * @param modifier Modificadores
 */
@Composable
private fun SearchContent(
    songs: List<Song>,
    isSearching: Boolean,
    query: String,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    if (songs.isEmpty() && isSearching) {
        // Estado vacío: búsqueda sin resultados
        EmptySearchState(query = query, modifier = modifier)
    } else {
        // Lista de resultados (o todas las canciones si no está buscando)
        SongList(
            songs = songs,
            onSongClick = onSongClick,
            modifier = modifier
        )
    }
}

/**
 * Estado cuando no hay resultados de búsqueda.
 *
 * @param query Texto buscado (para mostrar en el mensaje)
 * @param modifier Modificadores
 *
 * EMPTY STATES:
 * -------------
 * Es importante manejar estados vacíos de forma amigable.
 * En lugar de mostrar una lista vacía, comunicamos al usuario
 * qué pasó y posiblemente qué puede hacer.
 */
@Composable
private fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No se encontraron resultados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "para \"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Lista de canciones en formato vertical.
 *
 * @param songs Lista de canciones
 * @param onSongClick Callback para click en canción
 * @param modifier Modificadores
 *
 * DIFERENCIA CON HOMESCREEN:
 * --------------------------
 * HomeScreen usa LazyColumn + LazyRow (grid de categorías)
 * SearchScreen usa solo LazyColumn (lista simple)
 *
 * Cada pantalla elige el layout más apropiado para su caso de uso:
 * - Home: Explorar por categorías → grid horizontal por categoría
 * - Search: Encontrar canción específica → lista rápida de escanear
 */
@Composable
private fun SongList(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = songs,
            key = { it.id }
        ) { song ->
            SongListItem(
                song = song,
                onClick = { onSongClick(song) }
            )
        }
    }
}

/**
 * Item individual de la lista de canciones.
 *
 * @param song Datos de la canción
 * @param onClick Callback para click
 *
 * DISEÑO DE LISTA vs GRID:
 * ------------------------
 * En HomeScreen usamos SongCard (vertical: cover arriba, texto abajo)
 * Aquí usamos SongListItem (horizontal: cover izquierda, texto derecha)
 *
 * El diseño horizontal es mejor para listas porque:
 * - Aprovecha mejor el ancho de pantalla
 * - Más información visible sin scroll
 * - Más rápido de escanear visualmente
 */
@Composable
private fun SongListItem(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cover pequeño
        SongCoverMock(
            colorSeed = song.colorSeed,
            size = 56.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Información de la canción
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
