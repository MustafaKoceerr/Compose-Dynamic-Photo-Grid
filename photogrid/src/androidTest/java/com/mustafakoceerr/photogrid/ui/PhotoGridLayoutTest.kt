package com.mustafakoceerr.photogrid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafakoceerr.photogrid.model.PhotoGridImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class PhotoGridLayoutTest {

    @get:Rule
    val rule = createComposeRule()

    /** Phone portrait, foldable, small tablet, tablet landscape. */
    private val widths = listOf(360.dp, 600.dp, 840.dp, 1280.dp)

    // Landscape, Wide, Panorama, Portrait, Square: the same set the previews use.
    private val gallery = listOf(1.5f, 1.66f, 2f, 0.5f, 1f).map(::image)

    private fun image(aspectRatio: Float) =
        PhotoGridImage(model = android.R.drawable.ic_menu_gallery, aspectRatio = aspectRatio)

    private fun bounds(index: Int): DpRect =
        rule.onNodeWithTag(PhotoGridTestTags.image(index)).getUnclippedBoundsInRoot()

    /**
     * Size of one pixel in dp inside the forced window. ForcedSize shrinks the density when the
     * forced size is larger than the real screen, so on a small or landscape device a pixel can be
     * well over 1dp; tolerances are expressed in pixels to stay valid on every device.
     */
    private var pixel = 1.dp

    private fun assertClose(message: String, expected: Dp, actual: Dp, tolerance: Dp = pixel * 1.5f) {
        assertTrue("$message: expected $expected but was $actual", abs((expected - actual).value) <= tolerance.value)
    }

    @Composable
    private fun ForcedWidth(width: Dp, content: @Composable () -> Unit) {
        DeviceConfigurationOverride(DeviceConfigurationOverride.ForcedSize(DpSize(width, ForcedHeight))) {
            pixel = (1f / LocalDensity.current.density).dp
            // Unbounded height, like the editor's scroll and lazy grid cards in real use.
            Column(Modifier.verticalScroll(rememberScrollState())) { content() }
        }
    }

    /** Renders [content] in a window exactly [width] wide. */
    private fun setContentWithWidth(width: Dp, content: @Composable () -> Unit) {
        rule.setContent { ForcedWidth(width, content) }
    }

    private fun assertJustifiedRow(width: Dp, indices: List<Int>, ratios: List<Float>, top: Dp? = null) {
        val rects = indices.map(::bounds)
        val height = rects.first().height
        assertClose("row starts at the left edge", 0.dp, rects.first().left)
        assertClose("row ends at the right edge", width, rects.last().right)
        rects.forEachIndexed { i, rect ->
            assertClose("equal heights in row $indices", height, rect.height)
            assertClose("same top in row $indices", rects.first().top, rect.top)
            if (top != null) assertClose("row top", top, rect.top)
            if (i > 0) assertClose("4dp gap", rects[i - 1].right + 4.dp, rect.left)
        }
        // Widths proportional to aspect ratio <=> every image shows at its natural ratio.
        rects.zip(ratios).forEach { (rect, ratio) ->
            // Width is off by at most 1px and height by half a pixel after rounding.
            assertClose("width/ratio for ratio $ratio", rect.height, rect.width / ratio, tolerance = pixel * (1f + 1f / ratio))
        }
    }

    @Test
    fun fiveImages_twoOnTop_threeBelow_justifiedAtEveryWidth() {
        // A compose rule allows one setContent per test, so the width is swept through state.
        var width by mutableStateOf(widths.first())
        rule.setContent {
            ForcedWidth(width) { PhotoGrid(images = gallery) }
        }

        widths.forEach { newWidth ->
            width = newWidth
            rule.waitForIdle()

            // Newest two (indices 3, 4) on top, then 0, 1, 2.
            assertJustifiedRow(newWidth, listOf(3, 4), listOf(0.5f, 1f), top = 0.dp)
            val topRowBottom = bounds(3).bottom
            assertJustifiedRow(newWidth, listOf(0, 1, 2), listOf(1.5f, 1.66f, 2f), top = topRowBottom + 4.dp)
        }
    }

    @Test
    fun maxRowHeight_capsTallRows_whileStillFillingTheWidth() {
        // Regression: two portraits on a 1280dp tablet used to become ~1270dp tall.
        setContentWithWidth(1280.dp) {
            PhotoGrid(images = listOf(image(0.5f), image(0.5f)), maxRowHeight = 300.dp)
        }
        val left = bounds(0)
        val right = bounds(1)
        assertClose("height capped", 300.dp, left.height)
        assertClose("height capped", 300.dp, right.height)
        assertClose("fills width", 1280.dp, right.right)
        assertClose("equal widths", left.width, right.width)
    }

    @Test
    fun singleImage_usesItsAspectRatio_notTheDrawableSize() {
        setContentWithWidth(360.dp) { PhotoGrid(images = listOf(image(2f))) }
        val rect = bounds(0)
        assertClose("full width", 360.dp, rect.width)
        assertClose("height from aspect ratio", 180.dp, rect.height)
    }

    @Test
    fun notePhotoGrid_capsLonePortrait() {
        setContentWithWidth(300.dp) { NotePhotoGrid(images = listOf(image(0.5f))) }
        val rect = bounds(0)
        assertClose("width", 300.dp, rect.width)
        assertClose("height capped at width / 0.75", 400.dp, rect.height)
    }

    @Test
    fun notePhotoGrid_showsOnlyTheNewestSix() {
        val images = (0 until 8).map { image(1f) }
        setContentWithWidth(360.dp) { NotePhotoGrid(images = images) }

        rule.onNodeWithTag(PhotoGridTestTags.image(0)).assertDoesNotExist()
        rule.onNodeWithTag(PhotoGridTestTags.image(1)).assertDoesNotExist()
        (2 until 8).forEach { rule.onNodeWithTag(PhotoGridTestTags.image(it)).assertExists() }
        // Newest chunk (5, 6, 7) on top.
        assertTrue(bounds(5).top < bounds(2).top)
    }

    @Test
    fun invalidAspectRatios_renderAsSquares_insteadOfCrashing() {
        setContentWithWidth(360.dp) {
            PhotoGrid(
                images = listOf(image(0f), image(Float.NaN), image(-3f)),
                spacing = 0.dp,
            )
        }
        (0..2).forEach { index ->
            val rect = bounds(index)
            assertClose("square width", 120.dp, rect.width)
            assertClose("square height", 120.dp, rect.height)
        }
    }

    @Test
    fun emptyList_rendersNothing() {
        setContentWithWidth(360.dp) { PhotoGrid(images = emptyList()) }
        rule.onNodeWithTag(PhotoGridTestTags.image(0)).assertDoesNotExist()
    }

    @Test
    fun rightToLeft_placesFirstImageOnTheRight() {
        setContentWithWidth(360.dp) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                PhotoGrid(images = listOf(image(1f), image(1f)))
            }
        }
        assertTrue(bounds(0).left > bounds(1).left)
        assertClose("first image at the right edge", 360.dp, bounds(0).right)
    }

    @Test
    fun editorImageGrid_neverExceeds65PercentOfWindowHeight() {
        var maxAllowed = 0.dp
        rule.setContent {
            val windowHeightPx = LocalWindowInfo.current.containerSize.height
            maxAllowed = with(LocalDensity.current) { (windowHeightPx * 0.65f).toDp() }
            // Two portraits, and a lone very tall image on top.
            EditorImageGrid(images = listOf(image(0.5f), image(0.5f), image(0.4f), image(0.3f), image(0.2f)))
        }
        rule.waitForIdle()
        assertTrue("window height must be known", maxAllowed > 0.dp)
        (0..4).forEach { index ->
            val height = bounds(index).height
            assertTrue("image $index height $height > $maxAllowed", height <= maxAllowed + 1.dp)
        }
    }
}

private val ForcedHeight = 1000.dp
