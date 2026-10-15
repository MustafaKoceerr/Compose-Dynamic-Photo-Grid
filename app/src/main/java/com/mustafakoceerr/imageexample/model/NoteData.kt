package com.mustafakoceerr.imageexample.model

import androidx.compose.runtime.Immutable
import com.mustafakoceerr.photogrid.model.PhotoGridImage

/** A single note. [images] are ordered oldest first; the grid shows the newest on top. */
@Immutable
data class NoteData(
    val id: Int,
    val images: List<PhotoGridImage> = emptyList(),
    val title: String = "",
    val content: String = "",
    val hasSound: Boolean = false,
)
