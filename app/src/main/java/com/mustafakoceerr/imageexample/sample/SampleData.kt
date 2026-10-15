package com.mustafakoceerr.imageexample.sample

import com.mustafakoceerr.imageexample.R
import com.mustafakoceerr.imageexample.model.NoteData
import com.mustafakoceerr.photogrid.model.PhotoGridImage

/**
 * Sample data shared by the demo app and the previews.
 *
 * The permutations follow one rule: for every group size (5..1) each of the five aspect ratios
 * is the LAST image once. The grid puts the newest (last) images on top, so this covers how
 * every aspect ratio behaves in the top row.
 */

val SampleGallery = listOf(
    PhotoGridImage(R.drawable.resim1, 1.5f, "Landscape"),
    PhotoGridImage(R.drawable.resim2, 1.66f, "Wide"),
    PhotoGridImage(R.drawable.resim3, 2.0f, "Panorama"),
    PhotoGridImage(R.drawable.resim4, 0.5f, "Portrait"),
    PhotoGridImage(R.drawable.resim5, 1.0f, "Square"),
)

data class SampleCase(val title: String, val imageIndices: List<Int>) {
    val images: List<PhotoGridImage> get() = imageIndices.map { SampleGallery[it] }
}

data class SampleGroup(val title: String, val cases: List<SampleCase>)

private fun case(title: String, vararg indices: Int) = SampleCase(title, indices.toList())

val SamplePermutationGroups = listOf(
    SampleGroup(
        "5 images (top row: 2)",
        listOf(
            case("Ends w/ Landscape (AR 1.5)", 1, 2, 3, 4, 0),
            case("Ends w/ Wide (AR 1.66)", 0, 2, 3, 4, 1),
            case("Ends w/ Panorama (AR 2.0)", 0, 1, 3, 4, 2),
            case("Ends w/ Portrait (AR 0.5)", 0, 1, 2, 4, 3),
            case("Ends w/ Square (AR 1.0)", 0, 1, 2, 3, 4),
        ),
    ),
    SampleGroup(
        "4 images (top row: 1)",
        listOf(
            case("Ends w/ Landscape", 1, 2, 3, 0),
            case("Ends w/ Wide", 0, 2, 4, 1),
            case("Ends w/ Panorama", 0, 1, 4, 2),
            case("Ends w/ Portrait", 0, 1, 4, 3), // A lone portrait on top: the critical case.
            case("Ends w/ Square", 0, 1, 2, 4),
        ),
    ),
    SampleGroup(
        "3 images (top row: 3)",
        listOf(
            case("Ends w/ Landscape", 1, 2, 0),
            case("Ends w/ Wide", 0, 2, 1),
            case("Ends w/ Panorama", 0, 3, 2),
            case("Ends w/ Portrait", 1, 4, 3),
            case("Ends w/ Square", 2, 3, 4),
        ),
    ),
    SampleGroup(
        "2 images",
        listOf(
            case("Ends w/ Landscape", 1, 0),
            case("Ends w/ Wide", 2, 1),
            case("Ends w/ Panorama", 3, 2),
            case("Ends w/ Portrait", 4, 3),
            case("Ends w/ Square", 0, 4),
        ),
    ),
    SampleGroup(
        "1 image",
        listOf(
            case("Landscape", 0),
            case("Wide", 1),
            case("Panorama", 2),
            case("Portrait", 3),
            case("Square", 4),
        ),
    ),
    SampleGroup(
        "More than 6 images (cards show the newest 6)",
        listOf(
            case("8 images", 0, 1, 2, 3, 4, 0, 1, 3),
            case("10 images", 4, 3, 2, 1, 0, 4, 3, 2, 1, 0),
        ),
    ),
)

/** Every permutation as a note, mixed with text-only and voice notes like a real board. */
fun buildSampleNotes(): List<NoteData> {
    var nextId = 1
    val imageNotes = SamplePermutationGroups.flatMap { group ->
        group.cases.map { case ->
            NoteData(
                id = nextId++,
                title = "${case.imageIndices.size} imgs - ${case.title}",
                content = "Testing layout with ${case.imageIndices.size} images.",
                images = case.images,
            )
        }
    }
    val textNotes = listOf(
        NoteData(id = nextId++, title = "Shopping", content = "Milk\nEggs\nBread\nCoffee beans\nOlive oil\nTomatoes"),
        NoteData(id = nextId++, content = "Short note without a title."),
        NoteData(id = nextId++, title = "Voice memo", hasSound = true),
        NoteData(
            id = nextId++,
            title = "A long note",
            content = "This note has a lot of text so the card is clamped to four lines and " +
                "ends with an ellipsis instead of growing forever in the staggered grid.",
        ),
        NoteData(id = nextId++, title = "Photo + voice", images = listOf(SampleGallery[4]), hasSound = true),
        NoteData(id = nextId++, title = "Images only", images = listOf(SampleGallery[2], SampleGallery[0])),
    )
    // Sprinkle the text notes between the image notes so the masonry gets mixed heights.
    val imageChunks = imageNotes.chunked(5)
    return List(maxOf(imageChunks.size, textNotes.size)) { i ->
        imageChunks.getOrElse(i) { emptyList() } + listOfNotNull(textNotes.getOrNull(i))
    }.flatten()
}
