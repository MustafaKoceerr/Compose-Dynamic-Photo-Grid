package com.mustafakoceerr.photogrid.model

import androidx.compose.runtime.Immutable

/**
 * A single image shown in a photo grid.
 *
 * @param model anything Coil can load: a drawable resource id, `Uri`, `File` or URL string.
 * @param aspectRatio width / height of the image. Invalid values (zero, negative, NaN) are
 * rendered as square instead of crashing.
 * @param contentDescription accessibility label; `null` marks the image as decorative.
 */
@Immutable
data class PhotoGridImage(
    val model: Any,
    val aspectRatio: Float,
    val contentDescription: String? = null,
)
