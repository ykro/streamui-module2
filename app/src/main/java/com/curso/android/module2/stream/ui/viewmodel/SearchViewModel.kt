package com.curso.android.module2.stream.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.curso.android.module2.stream.data.model.Song
import com.curso.android.module2.stream.data.repository.MockMusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ================================================================================
 * SEARCH VIEW MODEL - Lógica de Búsqueda
 * ================================================================================
 *
 * Este ViewModel demuestra cómo manejar EVENTOS de la UI (input del usuario)
 * y actualizar el ESTADO en respuesta.
 *
 * COMPARACIÓN CON HOME VIEW MODEL:
 * --------------------------------
 * - HomeViewModel: Carga datos una vez al iniciar
 * - SearchViewModel: Responde a eventos continuos (cada tecla del usuario)
 *
 * FLUJO UDF EN BÚSQUEDA:
 * ----------------------
 *
 *     Usuario escribe "rock"
 *            │
 *            ▼
 *     ┌──────────────┐
 *     │  SearchScreen │ ─── onQueryChange("rock") ───▶ ┌────────────────┐
 *     └──────────────┘                                  │ SearchViewModel │
 *            ▲                                          └────────────────┘
 *            │                                                   │
 *            │                                          updateQuery("rock")
 *            │                                                   │
 *            │                                                   ▼
 *            │                                          Filtrar canciones
 *            │                                                   │
 *            │◀─────── StateFlow emite nuevo estado ────────────┘
 *            │         (query="rock", results=[...])
 *            │
 *     UI se recompone con resultados
 *
 * El flujo siempre es UNIDIRECCIONAL:
 * 1. UI envía evento (onQueryChange)
 * 2. ViewModel procesa y actualiza estado
 * 3. UI observa el nuevo estado y se recompone
 */

/**
 * Estado de la pantalla de búsqueda.
 *
 * A diferencia de HomeUiState (sealed interface con Loading/Success/Error),
 * aquí usamos una data class simple porque:
 * - No hay carga asíncrona (filtrado es instantáneo)
 * - Siempre tenemos un estado válido (query vacío = sin resultados)
 *
 * CUÁNDO USAR CADA ENFOQUE:
 * - Sealed interface: Cuando hay estados mutuamente excluyentes (Loading vs Success)
 * - Data class: Cuando el estado es una combinación de valores que coexisten
 *
 * @property query Texto actual de búsqueda
 * @property results Lista de canciones que coinciden con la búsqueda
 * @property allSongs Todas las canciones disponibles (para mostrar inicialmente)
 */
data class SearchUiState(
    val query: String = "",
    val results: List<Song> = emptyList(),
    val allSongs: List<Song> = emptyList()
) {
    /**
     * Propiedad computada: ¿Está el usuario buscando activamente?
     *
     * Las propiedades computadas son útiles para derivar información
     * del estado sin duplicar datos.
     */
    val isSearching: Boolean
        get() = query.isNotBlank()

    /**
     * Canciones a mostrar: resultados si está buscando, todas si no.
     */
    val displayedSongs: List<Song>
        get() = if (isSearching) results else allSongs
}

/**
 * ViewModel para la pantalla de búsqueda.
 *
 * @param repository Repositorio de música (inyectado por Koin)
 *
 * INYECCIÓN DE DEPENDENCIAS:
 * --------------------------
 * Igual que HomeViewModel, recibe el repositorio como parámetro.
 * Koin resuelve automáticamente esta dependencia porque ya está
 * registrado como singleton en AppModule.
 *
 * Esto demuestra que MÚLTIPLES ViewModels pueden compartir
 * el MISMO repositorio sin crear instancias duplicadas.
 */
class SearchViewModel(
    private val repository: MockMusicRepository
) : ViewModel() {

    /**
     * Estado interno mutable.
     *
     * Inicializamos con todas las canciones para mostrarlas
     * antes de que el usuario empiece a buscar.
     */
    private val _uiState = MutableStateFlow(
        SearchUiState(
            allSongs = repository.getAllSongs()
        )
    )

    /**
     * Estado expuesto a la UI (inmutable).
     */
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    /**
     * Actualiza la consulta de búsqueda.
     *
     * Este método es llamado por la UI cada vez que el usuario
     * escribe o borra caracteres en el campo de búsqueda.
     *
     * @param query Nuevo texto de búsqueda
     *
     * PATRÓN EVENT HANDLER:
     * --------------------
     * Este es un "event handler" - un método público que la UI
     * llama para notificar eventos del usuario.
     *
     * Convención de nombres:
     * - onXxx(): Para eventos de UI (onClick, onChange)
     * - updateXxx(): Para actualizar estado específico
     * - loadXxx(): Para cargar datos
     *
     * ```kotlin
     * // En la UI:
     * TextField(
     *     value = uiState.query,
     *     onValueChange = { viewModel.updateQuery(it) }
     * )
     * ```
     */
    fun updateQuery(query: String) {
        val results = if (query.isBlank()) {
            emptyList()
        } else {
            searchSongs(query)
        }

        /**
         * ACTUALIZACIÓN DE ESTADO
         * -----------------------
         * Usamos copy() para crear un nuevo estado con los valores actualizados.
         *
         * IMPORTANTE: Nunca modifiques el estado directamente.
         * Siempre crea una NUEVA instancia. Esto es esencial para que
         * Compose detecte cambios y recomponga la UI.
         *
         * ❌ MAL:  _uiState.value.query = query
         * ✅ BIEN: _uiState.value = _uiState.value.copy(query = query)
         */
        _uiState.value = _uiState.value.copy(
            query = query,
            results = results
        )
    }

    /**
     * Busca canciones que coincidan con la consulta.
     *
     * @param query Texto a buscar
     * @return Lista de canciones que coinciden
     *
     * La búsqueda es case-insensitive y busca en título Y artista.
     */
    private fun searchSongs(query: String): List<Song> {
        val lowercaseQuery = query.lowercase()

        return repository.getAllSongs().filter { song ->
            song.title.lowercase().contains(lowercaseQuery) ||
                    song.artist.lowercase().contains(lowercaseQuery)
        }
    }

    /**
     * Limpia la búsqueda actual.
     *
     * Método de conveniencia para resetear el estado.
     * La UI podría llamar esto cuando el usuario presiona
     * el botón "X" del campo de búsqueda.
     */
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            query = "",
            results = emptyList()
        )
    }
}
