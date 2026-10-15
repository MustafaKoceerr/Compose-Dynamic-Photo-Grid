package com.mustafakoceerr.photogrid.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.mustafakoceerr.photogrid.layout.arrangeRows
import com.mustafakoceerr.photogrid.model.PhotoGridImage

/**
 * Keep-style photo grid: the newest `size % 3` images sit alone on top, the rest follow in
 * justified rows of three (see [arrangeRows]). Images keep their aspect ratio relative to each
 * other and are center-cropped only when a row hits a height cap.
 *
 * @param images images ordered oldest first; the newest image is the last element.
 * @param maxImages only the newest [maxImages] images are shown.
 * @param maxRowHeight upper bound for a row's height.
 * @param minRowAspectRatio rows are never taller than `width / minRowAspectRatio`; `0` disables it.
 */
@Composable
fun PhotoGrid(
    images: List<PhotoGridImage>,
    modifier: Modifier = Modifier,
    maxImages: Int = Int.MAX_VALUE,
    spacing: Dp = PhotoGridDefaults.Spacing,
    maxRowHeight: Dp = Dp.Infinity,
    minRowAspectRatio: Float = 0f,
    imageShape: Shape = RectangleShape,
    placeholderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val rows = remember(images, maxImages) {
        arrangeRows(images.withIndex().toList(), maxImages)
    }
    if (rows.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        rows.forEach { row ->
            key(row.first().index) {
                JustifiedImageRow(
                    images = row,
                    spacing = spacing,
                    maxRowHeight = maxRowHeight,
                    minRowAspectRatio = minRowAspectRatio,
                    imageShape = imageShape,
                    placeholderColor = placeholderColor,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
