package com.mustafakoceerr.imageexample

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


/**
 * Notes Dashboard Architecture.
 * * Contains the LazyVerticalStaggeredGrid and the individual NoteCard components.
 * NoteCard encapsulates its own inner photo grid (NotePhotoGrid) which limits images
 * to a maximum of 6, displaying the most recent uploads first.
 * Images within the card inherit the card's rounded corners naturally.
 */

@Composable
fun NotesStaggeredGrid(
    notes: List<NoteData>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = notes,
            key = { it.id }
        ) { note ->
            NoteCard(note = note)
        }
    }
}

@Composable
fun NoteCard(
    note: NoteData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val hasTextContent = note.title.isNotEmpty() || note.content.isNotEmpty() || note.hasSound

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp,
            hoveredElevation = 4.dp
        )
    ) {
        Column {
            if (note.images.isNotEmpty()) {
                NotePhotoGrid(images = note.images)
            }

            if (hasTextContent) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (note.title.isNotEmpty()) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (note.content.isNotEmpty()) {
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (note.hasSound) {
                        Text(text = "🎵 Ses kaydı", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotePhotoGrid(images: List<NoteImage>) {
    val cappedImages = images.takeLast(6)
    val remainder = cappedImages.size % 3

    val topRow = if (remainder > 0) {
        listOf(cappedImages.takeLast(remainder))
    } else {
        emptyList()
    }

    val bottomRows = cappedImages.dropLast(remainder).chunked(3).reversed()
    val allRows = topRow + bottomRows

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        allRows.forEach { rowImages ->
            when (rowImages.size) {
                1 -> SinglePhotoRow(image = rowImages[0])
                2 -> DoublePhotoRow(image1 = rowImages[0], image2 = rowImages[1])
                3 -> TriplePhotoRow(
                    image1 = rowImages[0],
                    image2 = rowImages[1],
                    image3 = rowImages[2]
                )
            }
        }
    }
}

@Composable
private fun SinglePhotoRow(image: NoteImage) {
    Image(
        painter = painterResource(id = image.drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(image.aspectRatio)
    )
}

@Composable
private fun DoublePhotoRow(image1: NoteImage, image2: NoteImage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = image1.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image1.aspectRatio)
                .aspectRatio(image1.aspectRatio)
        )
        Image(
            painter = painterResource(id = image2.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image2.aspectRatio)
                .aspectRatio(image2.aspectRatio)
        )
    }
}

@Composable
private fun TriplePhotoRow(image1: NoteImage, image2: NoteImage, image3: NoteImage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = image1.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image1.aspectRatio)
                .aspectRatio(image1.aspectRatio)
        )
        Image(
            painter = painterResource(id = image2.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image2.aspectRatio)
                .aspectRatio(image2.aspectRatio)
        )
        Image(
            painter = painterResource(id = image3.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(image3.aspectRatio)
                .aspectRatio(image3.aspectRatio)
        )
    }
}