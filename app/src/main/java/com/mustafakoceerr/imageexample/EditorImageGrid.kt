package com.mustafakoceerr.imageexample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Editor Image Grid Architecture.
 * * Displays a dynamic masonry-style photo gallery for the Note Editor screen.
 * Uses a modulo algorithm to arrange images in rows of 3, 2, or 1.
 * Newest images are displayed at the top, and the remainder (mod 3) is isolated in the first row.
 */

@Composable
private fun getMaxGalleryImageHeight(): Dp {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    return with(density) { (windowInfo.containerSize.height * 0.65F).toDp() }
}

@Composable
fun EditorImageGrid(
    images: List<NoteImage>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return

    val remainder = images.size % 3

    val topRow = if (remainder > 0) {
        listOf(images.takeLast(remainder))
    } else {
        emptyList()
    }

    val bottomRows = images.dropLast(remainder).chunked(3).reversed()
    val allRows = topRow + bottomRows

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        allRows.forEach { rowImages ->
            when (rowImages.size) {
                1 -> ResponsiveSingleImageGrid(image = rowImages[0])
                2 -> DoubleImageGrid(image1 = rowImages[0], image2 = rowImages[1])
                3 -> TripleImageGrid(image1 = rowImages[0], image2 = rowImages[1], image3 = rowImages[2])
            }
        }
    }
}

@Composable
fun ResponsiveSingleImageGrid(image: NoteImage, modifier: Modifier = Modifier) {
    val maxAllowedHeight = getMaxGalleryImageHeight()
    val keepShape = RoundedCornerShape(12.dp)

    Image(
        painter = painterResource(id = image.drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxAllowedHeight)
            .clip(keepShape)
    )
}

@Composable
fun DoubleImageGrid(image1: NoteImage, image2: NoteImage, modifier: Modifier = Modifier) {
    val maxAllowedHeight = getMaxGalleryImageHeight()
    val keepShape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = image1.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image1.aspectRatio)
                .aspectRatio(image1.aspectRatio)
                .heightIn(max = maxAllowedHeight)
                .clip(keepShape)
        )
        Image(
            painter = painterResource(id = image2.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image2.aspectRatio)
                .aspectRatio(image2.aspectRatio)
                .heightIn(max = maxAllowedHeight)
                .clip(keepShape)
        )
    }
}

@Composable
fun TripleImageGrid(image1: NoteImage, image2: NoteImage, image3: NoteImage, modifier: Modifier = Modifier) {
    val maxAllowedHeight = getMaxGalleryImageHeight()
    val keepShape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = image1.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image1.aspectRatio)
                .aspectRatio(image1.aspectRatio)
                .heightIn(max = maxAllowedHeight)
                .clip(keepShape)
        )
        Image(
            painter = painterResource(id = image2.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image2.aspectRatio)
                .aspectRatio(image2.aspectRatio)
                .heightIn(max = maxAllowedHeight)
                .clip(keepShape)
        )
        Image(
            painter = painterResource(id = image3.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image3.aspectRatio)
                .aspectRatio(image3.aspectRatio)
                .heightIn(max = maxAllowedHeight)
                .clip(keepShape)
        )
    }
}