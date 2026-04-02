package com.mustafakoceerr.imageexample

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlin.collections.map

/**
 * Master Previews for the Staggered Grid and Note Cards.
 * Generates a comprehensive permutation set to test the masonry layout.
 * * CORE RULE: For every group size (5, 4, 3, 2, 1), generates exactly 5 examples
 * ensuring that each of the 5 distinct images (Landscape, Wide, Panorama, Portrait, Square)
 * acts as the LAST element in the list at least once.
 * Since our algorithm puts the last elements at the top, this perfectly tests
 * every aspect ratio's behavior at the top of the card.
 */

private fun buildPreviewNotes(): List<NoteData> {
    var idCounter = 1

    // Added 'title' so we can visually identify what rule the card is testing
    fun note(title: String, vararg indexes: Int): NoteData {
        return NoteData(
            id = idCounter++,
            title = title,
            content = "Testing layout with ${indexes.size} images.",
            images = indexes.map { SharedMockGallery[it] }
        )
    }

    return listOf(
        // --------------------------------------------------
        // 5 IMAGES (Remainder 2 -> Last 2 images go to top)
        // Each of the 5 images takes a turn being the absolute last element.
        // --------------------------------------------------
        note("5 Imgs - Ends w/ Landscape (AR 1.5)", 1, 2, 3, 4, 0),
        note("5 Imgs - Ends w/ Wide (AR 1.66)",     0, 2, 3, 4, 1),
        note("5 Imgs - Ends w/ Panorama (AR 2.0)",  0, 1, 3, 4, 2),
        note("5 Imgs - Ends w/ Portrait (AR 0.5)",  0, 1, 2, 4, 3),
        note("5 Imgs - Ends w/ Square (AR 1.0)",    0, 1, 2, 3, 4),

        // --------------------------------------------------
        // 4 IMAGES (Remainder 1 -> Last 1 image goes to top alone)
        // This is the ultimate test for the SinglePhotoRow height limits.
        // --------------------------------------------------
        note("4 Imgs - Ends w/ Landscape", 1, 2, 3, 0),
        note("4 Imgs - Ends w/ Wide",      0, 2, 4, 1),
        note("4 Imgs - Ends w/ Panorama",  0, 1, 4, 2),
        note("4 Imgs - Ends w/ Portrait",  0, 1, 4, 3), // Watch this one closely!
        note("4 Imgs - Ends w/ Square",    0, 1, 2, 4),

        // --------------------------------------------------
        // 3 IMAGES (Remainder 0 -> Last 3 images form a TripleRow)
        // Testing how different ending images affect the 3-grid balance.
        // --------------------------------------------------
        note("3 Imgs - Ends w/ Landscape", 1, 2, 0),
        note("3 Imgs - Ends w/ Wide",      0, 2, 1),
        note("3 Imgs - Ends w/ Panorama",  0, 3, 2),
        note("3 Imgs - Ends w/ Portrait",  1, 4, 3),
        note("3 Imgs - Ends w/ Square",    2, 3, 4),

        // --------------------------------------------------
        // 2 IMAGES (Remainder 2 -> Last 2 images form a DoubleRow)
        // Testing extreme contrasts side-by-side.
        // --------------------------------------------------
        note("2 Imgs - Ends w/ Landscape", 1, 0),
        note("2 Imgs - Ends w/ Wide",      2, 1),
        note("2 Imgs - Ends w/ Panorama",  3, 2),
        note("2 Imgs - Ends w/ Portrait",  4, 3),
        note("2 Imgs - Ends w/ Square",    0, 4),

        // --------------------------------------------------
        // 1 IMAGE (Remainder 1 -> SingleRow)
        // --------------------------------------------------
        note("1 Img - Landscape", 0),
        note("1 Img - Wide",      1),
        note("1 Img - Panorama",  2),
        note("1 Img - Portrait",  3),
        note("1 Img - Square",    4)
    )
}

@Preview(
    name = "Master Staggered Grid Layout",
    showBackground = true,
    widthDp = 360,
    heightDp = 1600 // Increased height so we can scroll through all 25 test cases easily
)
@Composable
fun PreviewNotesStaggeredGridMaster() {
    val notes = buildPreviewNotes()

    Scaffold { innerPadding ->
        NotesStaggeredGrid(
            notes = notes,
            modifier = Modifier.padding(innerPadding)
        )
    }
}