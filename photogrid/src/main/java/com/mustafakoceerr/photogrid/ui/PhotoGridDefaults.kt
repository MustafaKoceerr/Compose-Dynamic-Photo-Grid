package com.mustafakoceerr.photogrid.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object PhotoGridDefaults {
    /** Gap between images, both horizontally and between rows. */
    val Spacing = 4.dp

    /** Rounded corners of each image in the editor grid. */
    val EditorImageShape = RoundedCornerShape(12.dp)

    /** A row in the editor is never taller than this fraction of the window height. */
    const val EDITOR_MAX_ROW_HEIGHT_FRACTION = 0.65f

    /** Note cards only preview the newest images. */
    const val NOTE_MAX_IMAGES = 6

    /**
     * Rows inside a note card are never taller than `cardWidth / 0.75`, so a lone portrait photo
     * cannot stretch the card to twice its width.
     */
    const val NOTE_MIN_ROW_ASPECT_RATIO = 0.75f
}

/** Test tags for UI tests. The index is the image's position in the list passed to the grid. */
object PhotoGridTestTags {
    fun image(index: Int): String = "photogrid:image:$index"
}
