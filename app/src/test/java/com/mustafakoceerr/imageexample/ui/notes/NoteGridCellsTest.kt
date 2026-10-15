package com.mustafakoceerr.imageexample.ui.notes

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class NoteGridCellsTest {

    private val cells = NoteGridCells(minColumnWidth = 220.dp, minColumns = 2)

    /** Column sizes for a window [windowWidthDp] wide with the board's 8dp padding and spacing. */
    private fun columnsFor(windowWidthDp: Int, density: Float = 1f): IntArray = with(cells) {
        val d = Density(density)
        val padding = (16 * density).toInt()
        d.calculateCrossAxisCellSizes(
            availableSize = (windowWidthDp * density).toInt() - padding,
            spacing = (8 * density).toInt(),
        )
    }

    @Test
    fun `column count follows window width`() {
        mapOf(
            320 to 2, // small phone: still two columns
            360 to 2,
            411 to 2, // Pixel phone portrait
            600 to 2,
            840 to 3,
            800 to 3, // Pixel Tablet portrait
            914 to 3, // Pixel phone landscape
            1280 to 5, // Pixel Tablet landscape
            1920 to 8,
        ).forEach { (width, expected) ->
            assertEquals("width=$width", expected, columnsFor(width).size)
        }
    }

    @Test
    fun `columns plus spacing fill the available size exactly`() {
        for (density in listOf(1f, 2f, 2.625f)) {
            for (width in 300..1400 step 37) {
                val available = (width * density).toInt() - (16 * density).toInt()
                val spacing = (8 * density).toInt()
                val sizes = columnsFor(width, density)
                assertEquals("width=$width density=$density", available, sizes.sum() + spacing * (sizes.size - 1))
                // Columns differ by at most one pixel.
                assertEquals(true, sizes.max() - sizes.min() <= 1)
            }
        }
    }

    @Test
    fun `degenerate widths do not produce negative sizes`() {
        val sizes = with(cells) { Density(1f).calculateCrossAxisCellSizes(availableSize = 4, spacing = 8) }
        assertEquals(2, sizes.size)
        sizes.forEach { assertEquals(0, it) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero min columns is rejected`() {
        NoteGridCells(minColumnWidth = 100.dp, minColumns = 0)
    }

    @Test
    fun `equal configurations are equal`() {
        assertEquals(NoteGridCells(220.dp), NoteGridCells(220.dp))
        assertEquals(NoteGridCells(220.dp).hashCode(), NoteGridCells(220.dp).hashCode())
    }
}
