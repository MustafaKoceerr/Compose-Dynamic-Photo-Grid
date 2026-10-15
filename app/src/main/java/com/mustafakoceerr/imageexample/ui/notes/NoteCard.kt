package com.mustafakoceerr.imageexample.ui.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mustafakoceerr.imageexample.R
import com.mustafakoceerr.imageexample.model.NoteData
import com.mustafakoceerr.photogrid.ui.NotePhotoGrid

object NoteTestTags {
    const val CARD_PREFIX = "note:"
    fun card(id: Int): String = "$CARD_PREFIX$id"
}

/**
 * A note on the board. The photo grid sits flush at the top and is clipped by the card's own
 * rounded shape; text, if any, follows below.
 */
@Composable
fun NoteCard(
    note: NoteData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasTextContent = note.title.isNotEmpty() || note.content.isNotEmpty() || note.hasSound

    // The clickable Card overload is required for the ripple to follow the shape and for the
    // pressed/hovered elevations below to apply at all.
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag(NoteTestTags.card(note.id)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp,
            hoveredElevation = 4.dp,
        ),
    ) {
        if (note.images.isNotEmpty()) {
            NotePhotoGrid(images = note.images)
        }

        if (hasTextContent) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (note.title.isNotEmpty()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (note.content.isNotEmpty()) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (note.hasSound) {
                    Text(
                        text = stringResource(R.string.note_voice_recording),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
