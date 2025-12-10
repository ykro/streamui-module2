package com.curso.android.module2.stream.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.curso.android.module2.stream.data.model.Category
import com.curso.android.module2.stream.data.repository.MockMusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ================================================================================
 * HOME VIEW MODEL - Lógica de Presentación
 * ================================================================================
 *
 * PATRÓN MVVM (Model-View-ViewModel)
 * ----------------------------------
 * MVVM separa la aplicación en tres capas:
 *
 * 1. MODEL (data/):
 *    - Datos y lógica de negocio
 *    - Repository, Data Sources, Entities
 *    - No conoce la UI
 *
 * 2. VIEW (ui/screens/):
 *    - Composables que renderizan la UI
 *    - Observa el estado del ViewModel
 *    - Envía eventos de usuario al ViewModel
 *    - NO contiene lógica de negocio
 *
 * 3. VIEWMODEL (ui/viewmodel/):
 *    - Puente entre Model y View
 *    - Expone estado observable (StateFlow)
 *    - Procesa eventos de la UI
 *    - Sobrevive cambios de configuración (rotación)
 *
 * FLUJO DE DATOS (UDF - Unidirectional Data Flow):
 * ------------------------------------------------
 *
 *     ┌─────────────────────────────────────────────┐
 *     │                                             │
 *     │    ┌──────────┐    State    ┌──────────┐   │
 *     │    │ViewModel │ ──────────▶ │   View   │   │
 *     │    └──────────┘             └──────────┘   │
 *     │         ▲                        │         │
 *     │         │       Events           │         │
 *     │         └────────────────────────┘         │
 *     │                                             │
 *     └─────────────────────────────────────────────┘
 *
 * - STATE fluye del ViewModel a la View (UI observa StateFlow)
 * - EVENTS fluyen de la View al ViewModel (clicks, inputs, etc.)
 * - NUNCA al revés: la View no modifica el estado directamente
 *
 * BENEFICIOS DE UDF:
 * 1. Predecibilidad: El estado solo cambia desde el ViewModel
 * 2. Debugging: Fácil rastrear cambios de estado
 * 3. Testing: Puedes verificar estados sin UI
 * 4. Compose: Se integra perfectamente con recomposición
 */

/**
 * Estado de la pantalla Home.
 *
 * Sealed interface permite representar múltiples estados posibles
 * de forma type-safe. Cada estado es una clase/objeto específico.
 *
 * ESTADOS TÍPICOS:
 * - Loading: Cargando datos
 * - Success: Datos cargados correctamente
 * - Error: Ocurrió un error
 *
 * En este ejemplo simplificado solo usamos Success ya que
 * los datos son síncronos (mock). En una app real tendrías
 * todos los estados.
 */
sealed interface HomeUiState {
    /**
     * Estado inicial mientras se cargan los datos.
     */
    data object Loading : HomeUiState

    /**
     * Datos cargados exitosamente.
     *
     * @property categories Lista de categorías con sus canciones
     */
    data class Success(
        val categories: List<Category>
    ) : HomeUiState

    /**
     * Error al cargar los datos.
     *
     * @property message Mensaje descriptivo del error
     */
    data class Error(
        val message: String
    ) : HomeUiState
}

/**
 * ViewModel para la pantalla Home.
 *
 * @param repository Repositorio de música (inyectado por Koin)
 *
 * INYECCIÓN DE DEPENDENCIAS:
 * --------------------------
 * El repository se pasa como parámetro del constructor.
 * Koin automáticamente lo proporciona cuando crea el ViewModel.
 *
 * Esto permite:
 * - Testing: Inyectar un mock repository en tests
 * - Flexibilidad: Cambiar la implementación sin modificar el ViewModel
 */
class HomeViewModel(
    private val repository: MockMusicRepository
) : ViewModel() {

    /**
     * STATE HOLDER (MutableStateFlow)
     * --------------------------------
     * MutableStateFlow es un holder de estado observable:
     * - Siempre tiene un valor (no nullable)
     * - Emite el valor actual a nuevos collectors
     * - Solo emite cuando el valor CAMBIA (distinctUntilChanged)
     *
     * Es PRIVADO porque solo el ViewModel debe modificar el estado.
     * La UI solo puede LEER a través de uiState (inmutable).
     */
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    /**
     * EXPOSED STATE (StateFlow)
     * --------------------------
     * Versión inmutable del estado, expuesta a la UI.
     *
     * asStateFlow() convierte MutableStateFlow a StateFlow (solo lectura).
     *
     * En Compose, la UI observa este StateFlow con collectAsState():
     * ```kotlin
     * val uiState by viewModel.uiState.collectAsState()
     * ```
     *
     * Cuando _uiState cambia, Compose automáticamente recompone
     * los composables que dependen de este valor.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * Inicialización del ViewModel.
     *
     * init {} se ejecuta cuando se crea el ViewModel.
     * Aquí cargamos los datos iniciales.
     *
     * NOTA: En una app real con datos remotos, usarías
     * viewModelScope.launch {} para operaciones asíncronas.
     */
    init {
        loadCategories()
    }

    /**
     * Carga las categorías desde el repositorio.
     *
     * En este ejemplo es síncrono porque usamos datos mock.
     * En una app real sería:
     *
     * ```kotlin
     * private fun loadCategories() {
     *     viewModelScope.launch {
     *         _uiState.value = HomeUiState.Loading
     *         try {
     *             val categories = repository.getCategories() // suspend fun
     *             _uiState.value = HomeUiState.Success(categories)
     *         } catch (e: Exception) {
     *             _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
     *         }
     *     }
     * }
     * ```
     */
    private fun loadCategories() {
        // Simula un estado de carga breve
        _uiState.value = HomeUiState.Loading

        // Carga los datos del repositorio
        val categories = repository.getCategories()

        // Actualiza el estado con los datos
        _uiState.value = HomeUiState.Success(categories)
    }

    /**
     * Recarga los datos (ejemplo de evento desde la UI).
     *
     * La UI llamaría a este método en respuesta a una acción
     * del usuario, como "pull to refresh".
     *
     * PATRÓN EVENT:
     * La UI envía eventos al ViewModel:
     * ```kotlin
     * Button(onClick = { viewModel.refresh() }) {
     *     Text("Refresh")
     * }
     * ```
     */
    fun refresh() {
        loadCategories()
    }
}
