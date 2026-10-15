package com.mustafakoceerr.imageexample.ui.notes

import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * Responsive column count for the notes board: as many columns as fit at [minColumnWidth], but
 * never fewer than [minColumns], so narrow phones still get a two-column masonry like Keep
 * (`StaggeredGridCells.Adaptive` would drop to a single column there).
 *
 * With the defaults: phone portrait 2, phone landscape / tablet portrait 3, tablet landscape 5.
 */
class NoteGridCells(
    private val minColumnWidth: Dp,
    private val minColumns: Int = 2,
) : StaggeredGridCells {

    init {
        require(minColumns >= 1) { "minColumns must be >= 1, was $minColumns" }
    }

    override fun Density.calculateCrossAxisCellSizes(availableSize: Int, spacing: Int): IntArray {
        val fittingColumns = (availableSize + spacing) / (minColumnWidth.roundToPx() + spacing)
        val count = maxOf(minColumns, fittingColumns)
        val cellsSpace = (availableSize - spacing * (count - 1)).coerceAtLeast(0)
        val baseSize = cellsSpace / count
        val remainder = cellsSpace % count
        return IntArray(count) { index -> baseSize + if (index < remainder) 1 else 0 }
    }

    override fun equals(other: Any?): Boolean =
        other is NoteGridCells && minColumnWidth == other.minColumnWidth && minColumns == other.minColumns

    override fun hashCode(): Int = 31 * minColumnWidth.hashCode() + minColumns
}
