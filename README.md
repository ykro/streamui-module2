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

## Presentación del Módulo

Todos los conceptos teóricos, diagramas de arquitectura y explicaciones detalladas (MVVM, UDF, DI, Navigation Type-Safe) se encuentran en los slides:

📄 [**Ver Presentación (Slides)**](slides/slides.md)


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
