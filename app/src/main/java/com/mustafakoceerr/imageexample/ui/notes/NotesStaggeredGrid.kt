package com.mustafakoceerr.imageexample.ui.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakoceerr.imageexample.model.NoteData

/** Minimum card width; see [NoteGridCells] for the resulting column counts. */
private val MinNoteColumnWidth = 220.dp

/** Keep-style masonry board of notes. The column count follows the available width. */
@Composable
fun NotesStaggeredGrid(
    notes: List<NoteData>,
    onNoteClick: (NoteData) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    LazyVerticalStaggeredGrid(
        columns = NoteGridCells(minColumnWidth = MinNoteColumnWidth),
        modifier = modifier.fillMaxSize(),
        state = state,
        contentPadding = contentPadding,
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = notes, key = { it.id }) { note ->
            NoteCard(note = note, onClick = { onNoteClick(note) })
        }
    }
}
