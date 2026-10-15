package com.mustafakoceerr.imageexample.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.mustafakoceerr.imageexample.R
import com.mustafakoceerr.imageexample.model.NoteData
import com.mustafakoceerr.imageexample.sample.buildSampleNotes
import com.mustafakoceerr.imageexample.ui.editor.EditorGalleryScreen
import com.mustafakoceerr.imageexample.ui.editor.NoteEditorScreen
import com.mustafakoceerr.imageexample.ui.notes.NotesStaggeredGrid

enum class DemoTab(@StringRes val title: Int) {
    Notes(R.string.tab_notes),
    Editor(R.string.tab_editor),
}

/**
 * Demo shell: a notes board and the editor permutation gallery. Tapping a note opens it in the
 * editor. Scroll positions are hoisted here so they survive opening and closing a note.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoApp(
    modifier: Modifier = Modifier,
    notes: List<NoteData> = remember { buildSampleNotes() },
) {
    var selectedTab by rememberSaveable { mutableStateOf(DemoTab.Notes) }
    var openNoteId by rememberSaveable { mutableStateOf<Int?>(null) }
    val notesGridState = rememberLazyStaggeredGridState()
    val editorListState = rememberLazyListState()

    val openNote = openNoteId?.let { id -> notes.firstOrNull { it.id == id } }
    if (openNote != null) {
        NoteEditorScreen(note = openNote, onBack = { openNoteId = null }, modifier = modifier)
        return
    }

    // The title bar scrolls away so short landscape windows keep most of the height for content.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    scrollBehavior = scrollBehavior,
                )
                PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
                    DemoTab.entries.forEach { tab ->
                        Tab(
                            selected = tab == selectedTab,
                            onClick = { selectedTab = tab },
                            text = { Text(stringResource(tab.title)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            DemoTab.Notes -> NotesStaggeredGrid(
                notes = notes,
                onNoteClick = { openNoteId = it.id },
                state = notesGridState,
                modifier = Modifier.padding(innerPadding),
            )
            DemoTab.Editor -> EditorGalleryScreen(
                state = editorListState,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
