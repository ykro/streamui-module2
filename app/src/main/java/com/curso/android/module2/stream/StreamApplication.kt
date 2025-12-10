package com.curso.android.module2.stream

import android.app.Application
import com.curso.android.module2.stream.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * ================================================================================
 * STREAM APPLICATION - Clase Application Personalizada
 * ================================================================================
 *
 * La clase Application es el punto de entrada de la aplicación Android.
 * Se crea ANTES que cualquier Activity, Service, o BroadcastReceiver.
 *
 * USOS COMUNES:
 * - Inicializar librerías de terceros (Koin, Firebase, Timber, etc.)
 * - Configurar handlers globales de errores
 * - Almacenar estado compartido de la aplicación
 *
 * CICLO DE VIDA:
 * --------------
 * 1. onCreate() se llama cuando la app se inicia (proceso creado)
 * 2. La instancia persiste mientras el proceso esté vivo
 * 3. onTerminate() se llama solo en emuladores (no en dispositivos reales)
 *
 * REGISTRO EN MANIFEST:
 * ---------------------
 * Para usar una Application personalizada, debe registrarse en AndroidManifest:
 * ```xml
 * <application
 *     android:name=".StreamApplication"
 *     ...>
 * ```
 *
 * ================================================================================
 * INICIALIZACIÓN DE KOIN
 * ================================================================================
 *
 * Koin se inicializa en onCreate() antes de que cualquier componente
 * necesite dependencias. El proceso es:
 *
 * 1. startKoin { } - Inicia el contenedor de DI
 * 2. androidContext() - Proporciona el Context de Android
 * 3. modules() - Registra los módulos de dependencias
 *
 * Después de esto, cualquier parte de la app puede solicitar dependencias
 * usando koinInject(), koinViewModel(), etc.
 */
class StreamApplication : Application() {

    /**
     * Se llama cuando la aplicación se crea por primera vez.
     *
     * IMPORTANTE: Mantén onCreate() rápido.
     * Operaciones lentas aquí retrasan el inicio de la app.
     * Usa inicialización lazy o background threads si es necesario.
     */
    override fun onCreate() {
        super.onCreate()

        /**
         * INICIALIZACIÓN DE KOIN
         * ----------------------
         * startKoin es el punto de entrada de Koin.
         *
         * androidContext(this): Proporciona el Application Context.
         * Koin lo usa internamente para funciones que necesitan Context.
         *
         * androidLogger(): Habilita logging de Koin en Logcat.
         * Útil para debugging de dependencias.
         *
         * modules(appModule): Carga el módulo de dependencias definido
         * en di/AppModule.kt. Puedes cargar múltiples módulos aquí.
         *
         * MEJORES PRÁCTICAS:
         * ------------------
         * En apps grandes, organiza los módulos por feature:
         * ```kotlin
         * modules(
         *     coreModule,      // Dependencias compartidas
         *     networkModule,   // Retrofit, OkHttp
         *     databaseModule,  // Room
         *     homeModule,      // Feature Home
         *     playerModule     // Feature Player
         * )
         * ```
         */
        startKoin {
            // Contexto de Android para Koin
            androidContext(this@StreamApplication)

            // Logger de Koin (muestra info de DI en Logcat)
            androidLogger()

            // Registra los módulos de dependencias
            modules(appModule)
        }
    }
}
