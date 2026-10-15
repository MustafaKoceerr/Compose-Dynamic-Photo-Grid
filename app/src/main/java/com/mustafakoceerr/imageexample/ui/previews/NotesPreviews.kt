package com.mustafakoceerr.imageexample.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mustafakoceerr.imageexample.sample.buildSampleNotes
import com.mustafakoceerr.imageexample.ui.notes.NotesStaggeredGrid
import com.mustafakoceerr.imageexample.ui.theme.ImageExampleTheme

/** Every permutation from SampleData on the board, at phone and tablet widths. */

@Preview(name = "Notes board - phone", showBackground = true, widthDp = 360, heightDp = 1600)
@Preview(name = "Notes board - tablet landscape", showBackground = true, widthDp = 1280, heightDp = 1600)
@Composable
private fun NotesBoardPreview() {
    ImageExampleTheme {
        NotesStaggeredGrid(notes = buildSampleNotes(), onNoteClick = {})
    }
}
