package com.mustafakoceerr.photogrid.ui

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.mustafakoceerr.photogrid.layout.computeRowSize
import com.mustafakoceerr.photogrid.layout.sanitizeAspectRatio
import com.mustafakoceerr.photogrid.model.PhotoGridImage

/**
 * One justified row: all images share a height and fill the full width, measured in a single
 * pass (no subcomposition, so it is cheap inside lazy lists).
 */
@Composable
internal fun JustifiedImageRow(
    images: List<IndexedValue<PhotoGridImage>>,
    spacing: Dp,
    maxRowHeight: Dp,
    minRowAspectRatio: Float,
    imageShape: Shape,
    placeholderColor: Color,
    modifier: Modifier = Modifier,
) {
    val aspectRatios = images.map { it.value.aspectRatio }
    val measurePolicy = remember(aspectRatios, spacing, maxRowHeight, minRowAspectRatio) {
        justifiedRowMeasurePolicy(aspectRatios, spacing, maxRowHeight, minRowAspectRatio)
    }

    Layout(
        content = {
            images.forEach { (index, image) ->
                AsyncImage(
                    model = image.model,
                    contentDescription = image.contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .testTag(PhotoGridTestTags.image(index))
                        .clip(imageShape)
                        .background(placeholderColor)
                )
            }
        },
        measurePolicy = measurePolicy,
        modifier = modifier,
    )
}

private fun justifiedRowMeasurePolicy(
    aspectRatios: List<Float>,
    spacing: Dp,
    maxRowHeight: Dp,
    minRowAspectRatio: Float,
) = MeasurePolicy { measurables, constraints ->
    val spacingPx = spacing.roundToPx()
    val width = if (constraints.hasBoundedWidth) {
        constraints.maxWidth
    } else {
        // Unbounded (e.g. inside a horizontal scroll): size the row as if it had the fallback
        // height, so it still renders instead of throwing.
        val fallbackHeight = UnboundedRowHeight.toPx()
        (aspectRatios.sumOf { sanitizeAspectRatio(it).toDouble() } * fallbackHeight).toInt() +
            spacingPx * (aspectRatios.size - 1)
    }
    val maxHeightPx = if (maxRowHeight == Dp.Infinity) {
        Constraints.Infinity
    } else {
        maxRowHeight.roundToPx()
    }

    // A bounded incoming height (e.g. a fixed-size parent) also caps the row; images crop to fit.
    val rowSize = computeRowSize(
        availableWidth = width,
        spacing = spacingPx,
        aspectRatios = aspectRatios,
        maxHeight = minOf(maxHeightPx, constraints.maxHeight),
        minRowAspectRatio = minRowAspectRatio,
    )
    val placeables = measurables.mapIndexed { index, measurable ->
        measurable.measure(Constraints.fixed(rowSize.widths[index], rowSize.height))
    }

    layout(width, rowSize.height) {
        var x = 0
        placeables.forEach { placeable ->
            placeable.placeRelative(x, 0)
            x += placeable.width + spacingPx
        }
    }
}

private val UnboundedRowHeight = Dp(240f)
