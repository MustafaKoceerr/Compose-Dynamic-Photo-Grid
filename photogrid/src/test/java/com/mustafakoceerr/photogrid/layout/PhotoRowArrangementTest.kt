package com.mustafakoceerr.photogrid.layout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoRowArrangementTest {

    private fun items(count: Int) = (1..count).toList()

    // ---------------------------------------------------------------------------------------
    // arrangeRows
    // ---------------------------------------------------------------------------------------

    @Test
    fun `empty list produces no rows`() {
        assertEquals(emptyList<List<Int>>(), arrangeRows(emptyList<Int>()))
    }

    @Test
    fun `row sizes put the remainder on top and full rows of three below`() {
        val expectedRowSizes = mapOf(
            1 to listOf(1),
            2 to listOf(2),
            3 to listOf(3),
            4 to listOf(1, 3),
            5 to listOf(2, 3),
            6 to listOf(3, 3),
            7 to listOf(1, 3, 3),
            8 to listOf(2, 3, 3),
            9 to listOf(3, 3, 3),
            10 to listOf(1, 3, 3, 3),
            11 to listOf(2, 3, 3, 3),
            12 to listOf(3, 3, 3, 3),
        )
        expectedRowSizes.forEach { (count, sizes) ->
            assertEquals("count=$count", sizes, arrangeRows(items(count)).map { it.size })
        }
    }

    @Test
    fun `newest items are on top and order inside a row is preserved`() {
        // 1 is the oldest upload, 7 the newest.
        assertEquals(
            listOf(listOf(7), listOf(4, 5, 6), listOf(1, 2, 3)),
            arrangeRows(items(7)),
        )
        assertEquals(
            listOf(listOf(4, 5), listOf(1, 2, 3)),
            arrangeRows(items(5)),
        )
    }

    @Test
    fun `every item appears exactly once`() {
        for (count in 0..30) {
            val flattened = arrangeRows(items(count)).flatten()
            assertEquals("count=$count", items(count).toSet(), flattened.toSet())
            assertEquals("count=$count", count, flattened.size)
        }
    }

    @Test
    fun `maxItems keeps only the newest items`() {
        val rows = arrangeRows(items(10), maxItems = 6)
        assertEquals(listOf(listOf(8, 9, 10), listOf(5, 6, 7)), rows)
    }

    @Test
    fun `maxItems larger than the list keeps everything`() {
        assertEquals(arrangeRows(items(4)), arrangeRows(items(4), maxItems = 6))
    }

    @Test
    fun `maxItems of zero produces no rows`() {
        assertEquals(emptyList<List<Int>>(), arrangeRows(items(4), maxItems = 0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative maxItems is rejected`() {
        arrangeRows(items(4), maxItems = -1)
    }

    // ---------------------------------------------------------------------------------------
    // sanitizeAspectRatio
    // ---------------------------------------------------------------------------------------

    @Test
    fun `invalid aspect ratios fall back to square`() {
        listOf(0f, -1f, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY).forEach {
            assertEquals("ratio=$it", 1f, sanitizeAspectRatio(it))
        }
    }

    @Test
    fun `extreme aspect ratios are clamped`() {
        assertEquals(MIN_ASPECT_RATIO, sanitizeAspectRatio(0.001f))
        assertEquals(MAX_ASPECT_RATIO, sanitizeAspectRatio(1000f))
        assertEquals(1.5f, sanitizeAspectRatio(1.5f))
    }

    // ---------------------------------------------------------------------------------------
    // computeRowSize
    // ---------------------------------------------------------------------------------------

    @Test
    fun `widths and gaps fill the available width exactly`() {
        val ratioSets = listOf(
            listOf(1.5f),
            listOf(0.5f, 2f),
            listOf(1.5f, 1.66f, 2f),
            listOf(0.5f, 1f, 1.66f),
            listOf(0.33f, 0.33f, 0.33f),
        )
        for (width in listOf(100, 333, 360, 1079, 2560)) {
            ratioSets.forEach { ratios ->
                val size = computeRowSize(width, spacing = 11, aspectRatios = ratios)
                val total = size.widths.sum() + 11 * (ratios.size - 1)
                assertEquals("width=$width ratios=$ratios", width, total)
            }
        }
    }

    @Test
    fun `widths are proportional to aspect ratios so all heights match`() {
        val ratios = listOf(0.5f, 1f, 2f)
        val size = computeRowSize(availableWidth = 1012, spacing = 6, aspectRatios = ratios)
        // Content width 1000, sum of ratios 3.5 -> natural height 285.7
        assertEquals(286, size.height)
        size.widths.zip(ratios).forEach { (w, ratio) ->
            assertEquals(size.height.toFloat(), w / ratio, 2f / ratio)
        }
    }

    @Test
    fun `row height is capped by maxHeight while still filling the width`() {
        // Two portraits side by side on a wide tablet would naturally be 1270 tall.
        val size = computeRowSize(
            availableWidth = 1280,
            spacing = 10,
            aspectRatios = listOf(0.5f, 0.5f),
            maxHeight = 500,
        )
        assertEquals(500, size.height)
        assertEquals(listOf(635, 635), size.widths)
    }

    @Test
    fun `row height is capped by minRowAspectRatio`() {
        // Single portrait in a 300 wide card: natural height 600, capped at 300 / 0.75 = 400.
        val size = computeRowSize(
            availableWidth = 300,
            spacing = 4,
            aspectRatios = listOf(0.5f),
            minRowAspectRatio = 0.75f,
        )
        assertEquals(400, size.height)
        assertEquals(listOf(300), size.widths)
    }

    @Test
    fun `the tighter of the two caps wins`() {
        val size = computeRowSize(
            availableWidth = 300,
            spacing = 0,
            aspectRatios = listOf(0.5f),
            maxHeight = 350,
            minRowAspectRatio = 0.75f,
        )
        assertEquals(350, size.height)
    }

    @Test
    fun `invalid ratios do not break the computation`() {
        val size = computeRowSize(
            availableWidth = 300,
            spacing = 0,
            aspectRatios = listOf(0f, Float.NaN, -2f),
        )
        assertEquals(listOf(100, 100, 100), size.widths)
        assertEquals(100, size.height)
    }

    @Test
    fun `available width smaller than the gaps yields zero sized images`() {
        val size = computeRowSize(availableWidth = 5, spacing = 10, aspectRatios = listOf(1f, 1f))
        assertEquals(listOf(0, 0), size.widths)
        assertEquals(0, size.height)
    }

    @Test
    fun `empty row has zero height`() {
        val size = computeRowSize(availableWidth = 300, spacing = 4, aspectRatios = emptyList())
        assertEquals(RowSize(height = 0, widths = emptyList()), size)
    }

    @Test
    fun `height never exceeds the natural justified height`() {
        val size = computeRowSize(
            availableWidth = 300,
            spacing = 0,
            aspectRatios = listOf(3f),
            maxHeight = 1000,
            minRowAspectRatio = 0.75f,
        )
        assertEquals(100, size.height)
        assertTrue(size.widths.single() == 300)
    }
}
