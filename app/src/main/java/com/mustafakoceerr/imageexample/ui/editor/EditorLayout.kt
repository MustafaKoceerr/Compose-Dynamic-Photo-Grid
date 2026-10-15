package com.mustafakoceerr.imageexample.ui.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Readable editor width; wider windows (tablets, landscape) center the content column. */
internal val EditorMaxContentWidth = 600.dp

/** Fills the content column (up to [EditorMaxContentWidth]) and centers that column. */
internal fun Modifier.editorContentWidth(): Modifier =
    fillMaxWidth()
        .wrapContentWidth()
        .widthIn(max = EditorMaxContentWidth)
        .fillMaxWidth()
