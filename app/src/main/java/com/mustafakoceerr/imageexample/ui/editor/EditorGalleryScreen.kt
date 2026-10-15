package com.mustafakoceerr.imageexample.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mustafakoceerr.imageexample.sample.SampleGroup
import com.mustafakoceerr.imageexample.sample.SamplePermutationGroups
import com.mustafakoceerr.photogrid.ui.EditorImageGrid

/** Every editor grid permutation in one lazily composed list. */
@Composable
fun EditorGalleryScreen(
    modifier: Modifier = Modifier,
    groups: List<SampleGroup> = SamplePermutationGroups,
    state: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = state,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        groups.forEachIndexed { groupIndex, group ->
            if (groupIndex > 0) {
                item(key = "divider:$groupIndex") {
                    HorizontalDivider(modifier = Modifier.editorContentWidth())
                }
            }
            item(key = "header:$groupIndex", contentType = "header") {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.editorContentWidth(),
                )
            }
            items(
                items = group.cases,
                key = { case -> "case:$groupIndex:${case.title}" },
                contentType = { "case" },
            ) { case ->
                Column(
                    modifier = Modifier.editorContentWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = case.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    EditorImageGrid(images = case.images)
                }
            }
        }
    }
}
