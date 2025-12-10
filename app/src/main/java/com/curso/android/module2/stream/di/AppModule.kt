package com.curso.android.module2.stream.di

import com.curso.android.module2.stream.data.repository.MockMusicRepository
import com.curso.android.module2.stream.ui.viewmodel.HomeViewModel
import com.curso.android.module2.stream.ui.viewmodel.SearchViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * ================================================================================
 * APP MODULE - Configuración de Koin
 * ================================================================================
 *
 * INYECCIÓN DE DEPENDENCIAS (DI)
 * ------------------------------
 * La Inyección de Dependencias es un patrón donde los objetos reciben sus
 * dependencias desde el exterior en lugar de crearlas internamente.
 *
 * SIN DI (acoplado):
 * ```
 * class HomeViewModel {
 *     private val repository = MockMusicRepository() // ❌ Crea su propia dependencia
 * }
 * ```
 *
 * CON DI (desacoplado):
 * ```
 * class HomeViewModel(
 *     private val repository: MockMusicRepository // ✅ Recibe la dependencia
 * )
 * ```
 *
 * BENEFICIOS DE DI:
 * 1. Testing: Fácil inyectar mocks en pruebas
 * 2. Flexibilidad: Cambiar implementaciones sin modificar clientes
 * 3. Scope management: Koin maneja el ciclo de vida de los objetos
 * 4. Lazy initialization: Objetos se crean solo cuando se necesitan
 *
 * ================================================================================
 * KOIN DSL
 * ================================================================================
 *
 * Koin usa un DSL (Domain Specific Language) en Kotlin para configurar DI:
 *
 * - module { }     : Define un módulo de dependencias
 * - single { }     : Singleton - una sola instancia compartida
 * - factory { }    : Nueva instancia cada vez que se solicita
 * - viewModel { }  : ViewModel con scope de Android lifecycle
 *
 * FUNCIONES HELPER (*Of):
 * - singleOf(::Class)    : Equivalente a single { Class() } con autowiring
 * - factoryOf(::Class)   : Equivalente a factory { Class() } con autowiring
 * - viewModelOf(::Class) : Equivalente a viewModel { Class() } con autowiring
 *
 * "Autowiring" significa que Koin automáticamente resuelve los parámetros
 * del constructor buscando dependencias ya registradas.
 */

/**
 * Módulo principal de la aplicación.
 *
 * Define todas las dependencias que Koin debe gestionar.
 * En apps más grandes, dividirías esto en múltiples módulos:
 * - dataModule (repositorios, data sources)
 * - domainModule (use cases)
 * - presentationModule (ViewModels)
 */
val appModule = module {

    /**
     * REPOSITORY
     * ----------
     * singleOf crea un Singleton: una única instancia de MockMusicRepository
     * compartida en toda la aplicación.
     *
     * ¿Por qué Singleton?
     * - Los repositorios típicamente mantienen cache o estado
     * - Una instancia es suficiente para toda la app
     * - Evita duplicar datos en memoria
     */
    singleOf(::MockMusicRepository)

    /**
     * VIEWMODELS
     * ----------
     * viewModelOf registra el ViewModel con el scope adecuado de Android.
     *
     * Koin automáticamente:
     * 1. Detecta que HomeViewModel necesita MockMusicRepository en su constructor
     * 2. Resuelve esa dependencia del registro (el singleton creado arriba)
     * 3. Vincula el ViewModel al lifecycle del componente de Compose
     *
     * En Compose, usamos koinViewModel() para obtener la instancia:
     * ```
     * @Composable
     * fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) { ... }
     * ```
     */
    viewModelOf(::HomeViewModel)

    /**
     * SEARCHVIEWMODEL
     * ---------------
     * Segundo ViewModel que TAMBIÉN depende de MockMusicRepository.
     *
     * IMPORTANTE: Ambos ViewModels comparten la MISMA instancia del repository
     * porque está registrado como singleton (singleOf).
     *
     * Esto demuestra cómo Koin maneja múltiples dependientes:
     * - MockMusicRepository se crea UNA vez
     * - HomeViewModel recibe esa instancia
     * - SearchViewModel recibe la MISMA instancia
     *
     * Beneficios:
     * - Consistencia de datos entre pantallas
     * - Eficiencia de memoria
     * - Cache compartido (si el repository tuviera)
     */
    viewModelOf(::SearchViewModel)
}
