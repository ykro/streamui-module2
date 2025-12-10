package com.curso.android.module2.stream.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

/**
 * ================================================================================
 * SONG COVER MOCK - Componente Visual Generado por Código
 * ================================================================================
 *
 * Este componente genera una "carátula de álbum" visual sin necesidad de
 * archivos de imagen externos. Útil para:
 * - Prototipos y mockups rápidos
 * - Placeholders mientras cargan imágenes reales
 * - Situaciones donde no hay imagen disponible
 *
 * TÉCNICAS UTILIZADAS:
 * --------------------
 * 1. Brush.linearGradient: Crea un degradado de colores
 * 2. Color manipulation: Genera colores complementarios desde una semilla
 * 3. remember: Cachea el cálculo de colores para evitar recomputación
 *
 * COMPOSE UI CONCEPTS:
 * --------------------
 * - Modifier.background(brush): Aplica un gradiente como fondo
 * - Modifier.clip(shape): Recorta el contenido a una forma
 * - Box con contentAlignment: Centra el contenido hijo
 */

/**
 * Composable que muestra una carátula de álbum generada proceduralmente.
 *
 * @param colorSeed Semilla para generar los colores del gradiente.
 *                  Diferentes semillas producen diferentes combinaciones de colores.
 * @param size Tamaño del componente (ancho y alto iguales = cuadrado)
 * @param modifier Modificadores adicionales para personalizar el layout
 *
 * EJEMPLO DE USO:
 * ```kotlin
 * SongCoverMock(
 *     colorSeed = song.colorSeed,
 *     size = 120.dp,
 *     modifier = Modifier.padding(8.dp)
 * )
 * ```
 */
@Composable
fun SongCoverMock(
    colorSeed: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    /**
     * REMEMBER: Optimización de Performance
     * -------------------------------------
     * remember {} cachea el resultado del cálculo de colores.
     * Solo se recalcula si colorSeed cambia.
     *
     * Sin remember: Los colores se calcularían en CADA recomposición,
     * incluso si colorSeed no cambió. Esto es ineficiente.
     *
     * CLAVE DE CACHE: El parámetro de remember (colorSeed en este caso)
     * determina cuándo invalidar el cache.
     */
    val gradientColors = remember(colorSeed) {
        generateGradientColors(colorSeed)
    }

    Box(
        modifier = modifier
            .size(size)
            // clip ANTES de background para que el gradiente respete la forma
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        /**
         * Ícono superpuesto al gradiente.
         * Usamos Icons.Default de Material para evitar recursos externos.
         */
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Music note icon",
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(size * 0.4f) // 40% del tamaño del cover
        )
    }
}

/**
 * Genera una lista de colores para el gradiente basándose en una semilla.
 *
 * @param seed Valor numérico que determina los colores generados
 * @return Lista de 2-3 colores para usar en un gradiente
 *
 * ALGORITMO:
 * ----------
 * 1. Usa el seed como color base (ya es un color ARGB)
 * 2. Genera un color secundario ajustando el tono
 * 3. Opcionalmente agrega un tercer color para más variedad
 *
 * NOTA: Esta es una implementación simplificada.
 * En producción, podrías usar algoritmos más sofisticados
 * de teoría del color (análogos, complementarios, etc.)
 */
private fun generateGradientColors(seed: Int): List<Color> {
    // El seed ya es un color ARGB completo
    val baseColor = Color(seed)

    // Genera un color secundario "oscureciendo" el base
    // Multiplicamos los componentes RGB para oscurecer
    val darkerColor = Color(
        red = (baseColor.red * 0.6f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.6f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 0.6f).coerceIn(0f, 1f),
        alpha = 1f
    )

    // Genera un tercer color ajustando de forma diferente
    // Usamos el valor absoluto del hash para variar
    val accentColor = Color(
        red = (baseColor.red * 0.8f + 0.2f).coerceIn(0f, 1f),
        green = (baseColor.green * 0.7f).coerceIn(0f, 1f),
        blue = (baseColor.blue * 1.2f).coerceIn(0f, 1f),
        alpha = 1f
    )

    return listOf(baseColor, accentColor, darkerColor)
}

/**
 * Versión alternativa que genera colores completamente aleatorios
 * basándose solo en el seed como número.
 *
 * Útil cuando el seed NO es un color predefinido.
 */
@Composable
fun SongCoverMockFromSeed(
    seed: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val gradientColors = remember(seed) {
        // Usa el seed para generar colores pseudo-aleatorios pero deterministas
        val hash = seed.absoluteValue

        val hue1 = (hash % 360).toFloat()
        val hue2 = ((hash * 137) % 360).toFloat() // Golden angle approximation

        listOf(
            hslToColor(hue1, 0.7f, 0.5f),
            hslToColor(hue2, 0.6f, 0.4f),
            hslToColor((hue1 + 30) % 360, 0.8f, 0.3f)
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(colors = gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Music note icon",
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(size * 0.4f)
        )
    }
}

/**
 * Convierte valores HSL (Hue, Saturation, Lightness) a Color de Compose.
 *
 * HSL es más intuitivo para generar colores agradables:
 * - Hue (0-360): El "color" en el círculo cromático
 * - Saturation (0-1): Qué tan "vívido" es el color
 * - Lightness (0-1): Qué tan claro/oscuro es
 */
private fun hslToColor(hue: Float, saturation: Float, lightness: Float): Color {
    val c = (1 - kotlin.math.abs(2 * lightness - 1)) * saturation
    val x = c * (1 - kotlin.math.abs((hue / 60) % 2 - 1))
    val m = lightness - c / 2

    val (r, g, b) = when {
        hue < 60 -> Triple(c, x, 0f)
        hue < 120 -> Triple(x, c, 0f)
        hue < 180 -> Triple(0f, c, x)
        hue < 240 -> Triple(0f, x, c)
        hue < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f)
    )
}
