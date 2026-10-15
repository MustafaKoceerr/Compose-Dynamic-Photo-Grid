package com.mustafakoceerr.photogrid.layout

import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Pure, framework-free layout math behind the Keep-style photo grid.
 *
 * Kept free of Compose types so the rules can be unit tested on the JVM.
 */

/** Maximum number of images placed side by side in a single row. */
const val MAX_IMAGES_PER_ROW = 3

/** Aspect ratios outside this range are clamped so a single image cannot collapse a row. */
const val MIN_ASPECT_RATIO = 0.1f
const val MAX_ASPECT_RATIO = 10f

/**
 * Splits [items] (oldest first) into display rows, newest row first.
 *
 * Only the newest [maxItems] items are kept. The `size % 3` newest items form the top row on
 * their own and the rest is chunked into rows of three, with the newest chunk on top.
 * Item order inside a row is preserved.
 *
 * Example for 7 items `[1..7]`: `[[7], [4, 5, 6], [1, 2, 3]]`.
 */
fun <T> arrangeRows(items: List<T>, maxItems: Int = Int.MAX_VALUE): List<List<T>> {
    require(maxItems >= 0) { "maxItems must be >= 0, was $maxItems" }

    val capped = if (items.size > maxItems) items.takeLast(maxItems) else items
    if (capped.isEmpty()) return emptyList()

    val remainder = capped.size % MAX_IMAGES_PER_ROW
    val topRow = if (remainder > 0) listOf(capped.takeLast(remainder)) else emptyList()
    val fullRows = capped.dropLast(remainder).chunked(MAX_IMAGES_PER_ROW).asReversed()
    return topRow + fullRows
}

/**
 * Returns a usable aspect ratio (width / height): invalid values (zero, negative, NaN, infinite)
 * become `1f` and extreme ones are clamped to [MIN_ASPECT_RATIO]..[MAX_ASPECT_RATIO].
 */
fun sanitizeAspectRatio(aspectRatio: Float): Float =
    if (aspectRatio.isFinite() && aspectRatio > 0f) {
        aspectRatio.coerceIn(MIN_ASPECT_RATIO, MAX_ASPECT_RATIO)
    } else {
        1f
    }

/** Measured size of a single row, in pixels. */
data class RowSize(val height: Int, val widths: List<Int>)

/**
 * Computes a justified row: every image gets the same height and a width proportional to its
 * aspect ratio, and the widths plus gaps fill [availableWidth] exactly.
 *
 * The height is the natural justified height, capped by [maxHeight] and by
 * `availableWidth / minRowAspectRatio` (ignored when [minRowAspectRatio] is `0`). When capped,
 * the images keep filling the width and are expected to be center-cropped.
 */
fun computeRowSize(
    availableWidth: Int,
    spacing: Int,
    aspectRatios: List<Float>,
    maxHeight: Int = Int.MAX_VALUE,
    minRowAspectRatio: Float = 0f,
): RowSize {
    if (aspectRatios.isEmpty()) return RowSize(height = 0, widths = emptyList())

    val ratios = aspectRatios.map(::sanitizeAspectRatio)
    val contentWidth = (availableWidth - spacing * (ratios.size - 1)).coerceAtLeast(0)
    val ratioSum = ratios.sum()

    var heightCap = maxHeight.coerceAtLeast(0)
    if (minRowAspectRatio > 0f) {
        heightCap = min(heightCap, (availableWidth / minRowAspectRatio).roundToInt())
    }
    val height = min((contentWidth / ratioSum).roundToInt(), heightCap)

    // Cumulative rounding so rounding errors never leave a gap at the end of the row.
    var cumulativeRatio = 0f
    var previousEdge = 0
    val widths = ratios.mapIndexed { index, ratio ->
        cumulativeRatio += ratio
        val edge = if (index == ratios.lastIndex) {
            contentWidth
        } else {
            (contentWidth * cumulativeRatio / ratioSum).roundToInt()
        }
        (edge - previousEdge).also { previousEdge = edge }
    }
    return RowSize(height = height, widths = widths)
}
