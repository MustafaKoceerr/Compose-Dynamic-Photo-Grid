package com.mustafakoceerr.photogrid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mustafakoceerr.photogrid.model.PhotoGridImage

/**
 * Photo grid shown at the top of a note card. Shows at most the newest
 * [PhotoGridDefaults.NOTE_MAX_IMAGES] images with square corners, so the card's own shape clips
 * them, and caps tall rows so a portrait photo cannot stretch the card.
 */
@Composable
fun NotePhotoGrid(
    images: List<PhotoGridImage>,
    modifier: Modifier = Modifier,
) {
    PhotoGrid(
        images = images,
        modifier = modifier,
        maxImages = PhotoGridDefaults.NOTE_MAX_IMAGES,
        minRowAspectRatio = PhotoGridDefaults.NOTE_MIN_ROW_ASPECT_RATIO,
    )
}
