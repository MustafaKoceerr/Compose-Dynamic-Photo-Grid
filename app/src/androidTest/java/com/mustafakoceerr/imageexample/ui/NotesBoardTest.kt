package com.mustafakoceerr.imageexample.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mustafakoceerr.imageexample.R
import com.mustafakoceerr.imageexample.model.NoteData
import com.mustafakoceerr.imageexample.sample.SampleGallery
import com.mustafakoceerr.imageexample.ui.notes.NoteCard
import com.mustafakoceerr.imageexample.ui.notes.NoteTestTags
import com.mustafakoceerr.imageexample.ui.notes.NotesStaggeredGrid
import com.mustafakoceerr.imageexample.ui.theme.ImageExampleTheme
import com.mustafakoceerr.photogrid.ui.PhotoGridTestTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesBoardTest {

    @get:Rule
    val rule = createComposeRule()

    private val textNotes = (1..20).map { id ->
        NoteData(id = id, title = "Note $id", content = "Line\n".repeat(id % 4 + 1).trim())
    }

    private val isNoteCard = SemanticsMatcher("is a note card") { node ->
        node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(NoteTestTags.CARD_PREFIX) == true
    }

    /** Number of distinct card x positions currently laid out. */
    private fun columnCount(): Int =
        rule.onAllNodes(isNoteCard).fetchSemanticsNodes()
            .map { it.boundsInRoot.left.toInt() }
            .distinct()
            .size

    @Test
    fun columnCount_followsWindowWidth() {
        val expectedColumns = linkedMapOf(
            360.dp to 2, // phone portrait
            600.dp to 2,
            800.dp to 3, // tablet portrait
            914.dp to 3, // phone landscape
            1280.dp to 5, // tablet landscape
        )
        var width by mutableStateOf(expectedColumns.keys.first())
        rule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.ForcedSize(DpSize(width, 1600.dp))) {
                ImageExampleTheme {
                    NotesStaggeredGrid(notes = textNotes, onNoteClick = {})
                }
            }
        }

        expectedColumns.forEach { (newWidth, columns) ->
            width = newWidth
            rule.waitForIdle()
            assertEquals("width=$newWidth", columns, columnCount())
        }
    }

    @Test
    fun clickingACard_reportsThatNote() {
        var clicked: NoteData? = null
        rule.setContent {
            ImageExampleTheme {
                NotesStaggeredGrid(notes = textNotes, onNoteClick = { clicked = it })
            }
        }
        rule.onNodeWithTag(NoteTestTags.card(3)).performClick()
        assertEquals(3, clicked?.id)
    }

    @Test
    fun lonePortraitImage_doesNotStretchTheCard() {
        val note = NoteData(id = 1, images = listOf(SampleGallery[3])) // AR 0.5
        rule.setContent {
            ImageExampleTheme { NoteCard(note = note, onClick = {}) }
        }
        // A clickable Card merges its children for accessibility, so images live in the unmerged tree.
        val image = rule.onNodeWithTag(PhotoGridTestTags.image(0), useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        assertTrue(
            "portrait height ${image.height} should be capped at width / 0.75 (${image.width / 0.75f})",
            image.height.value <= image.width.value / 0.75f + 1f,
        )
    }

    @Test
    fun voiceNote_showsTheLocalizedLabel() {
        val label = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.note_voice_recording)
        rule.setContent {
            ImageExampleTheme { NoteCard(note = NoteData(id = 1, hasSound = true), onClick = {}) }
        }
        rule.onNodeWithText(label).assertIsDisplayed()
    }

    @Test
    fun noteWithManyImages_showsOnlyTheNewestSix() {
        val images = List(8) { SampleGallery[it % SampleGallery.size] }
        rule.setContent {
            ImageExampleTheme { NoteCard(note = NoteData(id = 1, images = images), onClick = {}) }
        }
        rule.onNodeWithTag(PhotoGridTestTags.image(1), useUnmergedTree = true).assertDoesNotExist()
        rule.onNodeWithTag(PhotoGridTestTags.image(2), useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithTag(PhotoGridTestTags.image(7), useUnmergedTree = true).assertIsDisplayed()
    }
}
