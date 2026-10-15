package com.mustafakoceerr.imageexample.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafakoceerr.imageexample.R
import com.mustafakoceerr.imageexample.model.NoteData
import com.mustafakoceerr.imageexample.sample.SampleGallery
import com.mustafakoceerr.imageexample.sample.SamplePermutationGroups
import com.mustafakoceerr.imageexample.ui.notes.NoteTestTags
import com.mustafakoceerr.imageexample.ui.theme.ImageExampleTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoAppTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val notes = listOf(
        NoteData(id = 1, title = "Trip photos", content = "Beach day", images = SampleGallery),
        NoteData(id = 2, title = "Groceries", content = "Milk"),
    )

    private fun string(id: Int) = rule.activity.getString(id)

    @Before
    fun setUp() {
        rule.setContent { ImageExampleTheme { DemoApp(notes = notes) } }
    }

    @Test
    fun openingANote_showsTheEditor_andBackButtonReturns() {
        rule.onNodeWithTag(NoteTestTags.card(1)).performClick()
        rule.onNodeWithText("Beach day").assertIsDisplayed()
        rule.onNodeWithContentDescription(string(R.string.navigate_back)).performClick()
        rule.onNodeWithTag(NoteTestTags.card(1)).assertIsDisplayed()
    }

    @Test
    fun systemBack_closesTheEditor() {
        rule.onNodeWithTag(NoteTestTags.card(2)).performClick()
        rule.onNodeWithTag(NoteTestTags.card(2)).assertDoesNotExist()
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.onNodeWithTag(NoteTestTags.card(2)).assertIsDisplayed()
    }

    @Test
    fun editorTab_showsThePermutationGallery() {
        rule.onNodeWithText(string(R.string.tab_editor)).performClick()
        rule.onNodeWithText(SamplePermutationGroups.first().title).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.tab_notes)).performClick()
        rule.onNodeWithTag(NoteTestTags.card(1)).assertIsDisplayed()
    }
}
