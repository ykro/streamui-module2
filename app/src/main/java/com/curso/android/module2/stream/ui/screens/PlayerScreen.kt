package com.curso.android.module2.stream.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.curso.android.module2.stream.data.model.Song
import com.curso.android.module2.stream.ui.components.SongCoverMock

/**
 * ================================================================================
 * PLAYER SCREEN - Pantalla de Reproducción
 * ================================================================================
 *
 * Muestra los detalles de una canción seleccionada con controles de reproducción
 * simulados.
 *
 * RECIBIENDO ARGUMENTOS DE NAVEGACIÓN:
 * ------------------------------------
 * Esta pantalla recibe un Song directamente (ya cargado desde el repository
 * en MainActivity). Alternativamente, podría recibir solo el songId y cargar
 * la canción aquí con un ViewModel dedicado.
 *
 * OPCIÓN A (usada aquí): Pasar el objeto completo
 * - Más simple para datos pequeños
 * - Evita una carga adicional
 * - Funciona bien si los datos ya están en memoria
 *
 * OPCIÓN B: Pasar solo el ID
 * - Mejor para datos que pueden cambiar
 * - Necesario si los datos son muy grandes
 * - Permite refresh desde la fuente de verdad
 *
 * SCAFFOLD:
 * ---------
 * Scaffold es el layout base de Material Design. Proporciona slots para:
 * - TopAppBar
 * - BottomBar
 * - FloatingActionButton
 * - Drawer
 * - SnackbarHost
 * - Content principal
 *
 * Maneja automáticamente el padding para evitar solapamientos.
 */

/**
 * Pantalla del reproductor de música.
 *
 * @param song Canción a mostrar (null si no se encontró)
 * @param onBackClick Callback para el botón de retroceso
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    song: Song?,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            /**
             * TOP APP BAR
             * -----------
             * Barra superior con:
             * - Ícono de navegación (flecha atrás)
             * - Título centrado
             *
             * TopAppBarDefaults.topAppBarColors() personaliza los colores.
             */
            TopAppBar(
                title = {
                    Text(
                        text = "Now Playing",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        /**
         * CONTENT CON PADDING
         * -------------------
         * paddingValues contiene el padding necesario para evitar
         * que el contenido se solape con la TopAppBar.
         *
         * SIEMPRE usa este padding en el contenido del Scaffold.
         */
        if (song != null) {
            PlayerContent(
                song = song,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            // Estado de error: canción no encontrada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Song not found",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Contenido principal del reproductor.
 *
 * @param song Canción a mostrar
 * @param modifier Modificadores del layout
 */
@Composable
private fun PlayerContent(
    song: Song,
    modifier: Modifier = Modifier
) {
    /**
     * ESTADO LOCAL: isPlaying
     * -----------------------
     * remember { mutableStateOf() } crea estado local del composable.
     *
     * 'by' es delegación que permite usar isPlaying directamente
     * en lugar de isPlaying.value
     *
     * Este estado es EFÍMERO: se pierde al salir de la pantalla.
     * Para estado persistente, usarías un ViewModel.
     *
     * NOTA: En una app real, el estado de reproducción estaría en
     * un servicio de audio y se observaría desde aquí.
     */
    var isPlaying by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cover grande
        SongCoverMock(
            colorSeed = song.colorSeed,
            size = 280.dp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Título de la canción
        Text(
            text = song.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Artista
        Text(
            text = song.artist,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Controles de reproducción
        PlaybackControls(
            isPlaying = isPlaying,
            onPlayPauseClick = { isPlaying = !isPlaying },
            onPreviousClick = { /* Simulado */ },
            onNextClick = { /* Simulado */ }
        )
    }
}

/**
 * Controles de reproducción (Previous, Play/Pause, Next).
 *
 * @param isPlaying Si está reproduciendo actualmente
 * @param onPlayPauseClick Callback para play/pause
 * @param onPreviousClick Callback para canción anterior
 * @param onNextClick Callback para siguiente canción
 *
 * COMPONENTES REUTILIZABLES:
 * --------------------------
 * Este componente está separado para:
 * 1. Reutilización en otras pantallas
 * 2. Testing independiente
 * 3. Código más organizado y legible
 */
@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Previous
        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous song",
                modifier = Modifier.size(32.dp)
            )
        }

        /**
         * BOTÓN PLAY/PAUSE
         * ----------------
         * FilledIconButton es un IconButton con fondo sólido.
         * Lo usamos para el botón principal (play/pause) para
         * darle más prominencia visual.
         *
         * El ícono cambia según el estado isPlaying.
         */
        FilledIconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Botón Next
        IconButton(
            onClick = onNextClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next song",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
