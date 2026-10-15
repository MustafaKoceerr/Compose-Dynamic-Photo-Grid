package com.mustafakoceerr.imageexample.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mustafakoceerr.imageexample.sample.buildSampleNotes
import com.mustafakoceerr.imageexample.ui.editor.EditorGalleryScreen
import com.mustafakoceerr.imageexample.ui.editor.NoteEditorScreen
import com.mustafakoceerr.imageexample.ui.theme.ImageExampleTheme

/** Every editor permutation from SampleData, at phone and tablet widths. */

@Preview(name = "Editor permutations - phone", showBackground = true, widthDp = 360, heightDp = 2000)
@Preview(name = "Editor permutations - tablet", showBackground = true, widthDp = 1280, heightDp = 2000)
@Composable
private fun EditorGalleryPreview() {
    ImageExampleTheme {
        EditorGalleryScreen()
    }
}

@Preview(name = "Note editor - phone", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Note editor - tablet", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun NoteEditorPreview() {
    ImageExampleTheme {
        NoteEditorScreen(note = buildSampleNotes().first(), onBack = {})
    }
}
