# StreamUI - Módulo 2: Arquitectura y Navegación

Proyecto educativo de Android que demuestra la implementación de arquitectura MVVM, inyección de dependencias con Koin, y navegación tipada con Navigation Compose.

## Screenshots

<p align="center">
  <img src="assets/screenshot_1.png" width="30%" />
  <img src="assets/screenshot_2.png" width="30%" />
  <img src="assets/screenshot_3.png" width="30%" />
</p>

### Demo Video
Puedes ver el funcionamiento de la aplicación en el siguiente video: [StreamUI Demo](assets/module2.webm)

## Conceptos Teóricos

### 1. Navigation Compose con Type-Safe Routes

Navigation Compose 2.8+ introduce **navegación tipada**, permitiendo definir rutas como clases Kotlin en lugar de strings.

#### Antes (propenso a errores):
```kotlin
// Definición
const val PLAYER_ROUTE = "player/{songId}"

// Navegación
navController.navigate("player/$songId") // Typo posible

// Extracción
val songId = backStackEntry.arguments?.getString("songId") // Puede ser null
```

#### Ahora (type-safe):
```kotlin
// Definición
@Serializable
data class PlayerDestination(val songId: String)

// Navegación
navController.navigate(PlayerDestination(songId = song.id)) // Compilador verifica

// Extracción
val destination = backStackEntry.toRoute<PlayerDestination>() // Tipo garantizado
val songId = destination.songId // Nunca null
```

**Ventajas:**
- Errores detectados en tiempo de compilación
- Autocompletado del IDE
- Refactoring seguro
- Sin errores de typo en nombres de argumentos

**Implementación en este proyecto:**
- `ui/navigation/Destinations.kt`: Define `HomeDestination`, `SearchDestination` (objects) y `PlayerDestination` (data class)
- `MainActivity.kt`: Configura `NavHost` con rutas tipadas

#### Navegación multi-nivel:
```
Home ──(search icon)──▶ Search ──(song click)──▶ Player
  ◀──────(back)────────   ◀────────(back)────────
```
El mismo `PlayerDestination` se reutiliza desde Home y Search.

---

### 2. MVVM (Model-View-ViewModel) y UDF (Unidirectional Data Flow)

MVVM separa la aplicación en tres capas:

```
┌─────────────────────────────────────────────┐
│                                             │
│    ┌──────────┐    State    ┌──────────┐   │
│    │ ViewModel │ ──────────▶ │   View   │   │
│    └──────────┘             └──────────┘   │
│         ▲                        │         │
│         │       Events           │         │
│         └────────────────────────┘         │
│                                             │
└─────────────────────────────────────────────┘
```

- **State**: Fluye del ViewModel a la View (UI observa `StateFlow`)
- **Events**: Fluyen de la View al ViewModel (clicks, inputs)

**StateFlow en este proyecto:**
```kotlin
// HomeViewModel.kt - Estado con sealed interface (Loading/Success/Error)
private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

// SearchViewModel.kt - Estado con data class (valores que coexisten)
private val _uiState = MutableStateFlow(SearchUiState())
val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
```

**Observación en Compose:**
```kotlin
// HomeScreen.kt / SearchScreen.kt
val uiState by viewModel.uiState.collectAsState()
```

**Manejo de eventos (SearchViewModel):**
```kotlin
// La UI envía eventos al ViewModel
fun updateQuery(query: String) {
    _uiState.value = _uiState.value.copy(
        query = query,
        results = searchSongs(query)
    )
}
```

---

### 3. Inyección de Dependencias con Koin

Koin es un framework ligero de DI para Kotlin que usa un DSL declarativo.

#### Sin DI (acoplado):
```kotlin
class HomeViewModel {
    private val repository = MockMusicRepository() // Crea su propia dependencia
}
```

#### Con DI (desacoplado):
```kotlin
class HomeViewModel(
    private val repository: MockMusicRepository // Recibe la dependencia
)
```

**Configuración en este proyecto:**

1. **Definición del módulo** (`di/AppModule.kt`):
```kotlin
val appModule = module {
    singleOf(::MockMusicRepository)  // Singleton compartido
    viewModelOf(::HomeViewModel)     // ViewModel con lifecycle awareness
    viewModelOf(::SearchViewModel)   // Segundo ViewModel, misma instancia del repository
}
```

**Múltiples ViewModels, mismo Repository:**
- `HomeViewModel` y `SearchViewModel` reciben la MISMA instancia de `MockMusicRepository`
- Esto garantiza consistencia de datos entre pantallas

2. **Inicialización** (`StreamApplication.kt`):
```kotlin
startKoin {
    androidContext(this@StreamApplication)
    modules(appModule)
}
```

3. **Uso en Compose** (`HomeScreen.kt`):
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel() // Koin inyecta automáticamente
) { ... }
```

---

### 4. Layouts Anidados (LazyColumn + LazyRow)

Estructura que permite scroll vertical de categorías, donde cada categoría tiene scroll horizontal de items.

```
┌──────────────────────────────────────────┐
│              LazyColumn                  │ ← Scroll vertical
│  ┌────────────────────────────────────┐  │
│  │  "Rock Classics"                   │  │
│  │  ┌────┬────┬────┬────┬────┬───▶   │  │ ← LazyRow
│  │  │ 🎵 │ 🎵 │ 🎵 │ 🎵 │ 🎵 │       │  │
│  │  └────┴────┴────┴────┴────┘       │  │
│  └────────────────────────────────────┘  │
│  ┌────────────────────────────────────┐  │
│  │  "Coding Focus"                    │  │
│  │  ┌────┬────┬────┬────┬────┬───▶   │  │
│  └────────────────────────────────────┘  │
│                   ▼                      │
└──────────────────────────────────────────┘
```

**Código:**
```kotlin
LazyColumn {
    items(categories) { category ->
        Text(category.name)
        LazyRow {
            items(category.songs) { song ->
                SongCard(song)
            }
        }
    }
}
```

---

## Estructura del Proyecto

```
com.curso.android.module2.stream/
├── StreamApplication.kt      # Inicialización de Koin
├── MainActivity.kt           # NavHost y navegación
├── data/
│   ├── model/
│   │   └── Models.kt         # Song, Category (@Serializable)
│   └── repository/
│       └── MockMusicRepository.kt
├── di/
│   └── AppModule.kt          # Módulo de Koin (2 ViewModels)
└── ui/
    ├── components/
    │   └── SongCoverMock.kt  # Cover generado por código
    ├── navigation/
    │   └── Destinations.kt   # Rutas type-safe (Home, Search, Player)
    ├── screens/
    │   ├── HomeScreen.kt     # Grid de categorías (LazyColumn + LazyRow)
    │   ├── SearchScreen.kt   # Búsqueda con TextField controlado
    │   └── PlayerScreen.kt   # Reproductor con controles
    ├── theme/
    │   └── Theme.kt
    └── viewmodel/
        ├── HomeViewModel.kt   # sealed interface UiState
        └── SearchViewModel.kt # data class UiState + eventos
```

---

## Versiones de Dependencias

| Dependencia | Versión |
|-------------|---------|
| Android Gradle Plugin | 8.8.0 |
| Compose BOM | 2025.12.00 |
| Navigation Compose | 2.9.6 |
| Koin BOM | 4.1.1 |
| Kotlinx Serialization | 1.9.0 |
| Kotlin | 2.2.0 |
| Target SDK | 36 |

---

## Cómo Ejecutar

1. Abrir el proyecto en Android Studio
2. Sincronizar Gradle
3. Ejecutar en un emulador o dispositivo (API 24+)

No se requieren assets externos: todas las imágenes son generadas por código usando gradientes y íconos de Material.

---

## Créditos

Proyecto generado usando [Claude Code](https://claude.com/code) y adaptado por **Adrián Catalán**.

---

## Recursos Adicionales

- [Type-Safe Navigation - Android Developers](https://developer.android.com/guide/navigation/design/type-safety)
- [Koin Documentation](https://insert-koin.io/docs/quickstart/android-compose/)
- [State and Jetpack Compose](https://developer.android.com/develop/ui/compose/state)
- [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation)
