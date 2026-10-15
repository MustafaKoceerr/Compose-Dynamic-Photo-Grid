package com.mustafakoceerr.photogrid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import com.mustafakoceerr.photogrid.model.PhotoGridImage

/**
 * Photo gallery for the note editor. Every image has rounded corners and no row is taller than
 * [maxRowHeightFraction] of the window height, so a portrait photo never fills the whole screen.
 */
@Composable
fun EditorImageGrid(
    images: List<PhotoGridImage>,
    modifier: Modifier = Modifier,
    maxRowHeightFraction: Float = PhotoGridDefaults.EDITOR_MAX_ROW_HEIGHT_FRACTION,
) {
    val windowHeightPx = LocalWindowInfo.current.containerSize.height
    val maxRowHeight = if (windowHeightPx > 0) {
        with(LocalDensity.current) { (windowHeightPx * maxRowHeightFraction).toDp() }
    } else {
        // Window size is not known yet (first frame, or some test hosts).
        Dp.Infinity
    }

    PhotoGrid(
        images = images,
        modifier = modifier,
        maxRowHeight = maxRowHeight,
        imageShape = PhotoGridDefaults.EditorImageShape,
    )
}
