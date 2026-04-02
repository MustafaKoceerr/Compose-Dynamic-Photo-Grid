package com.mustafakoceerr.imageexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Master Previews for the standalone Editor Image Grid.
 * Eliminates random repetitions by strictly enforcing a systematic test:
 * For every group size (5, 4, 3, 2, 1), each of the 5 distinct Aspect Ratios
 * (Landscape, Wide, Panorama, Portrait, Square) is placed at the LAST index.
 * Since the remainder algorithm pulls from the end of the list to build the top row,
 * this perfectly tests every image's behavior at the top of the editor screen.
 */

@Composable
private fun EditorTestCase(title: String, vararg indices: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        EditorImageGrid(images = indices.map { SharedMockGallery[it] })
    }
}

@Preview(
    name = "Master Editor Grid Permutations",
    showBackground = true,
    widthDp = 360,
    heightDp = 2000 // Scroll edebilmek için uzun tuttuk
)
@Composable
fun PreviewEditorImageGridMaster() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // --------------------------------------------------
            // 5 FOTOĞRAF (Kalan 2 -> Son 2 resim en tepeye 2'li olarak çıkar)
            // --------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("--- 5 IMAGES (Top Row: 2) ---", style = MaterialTheme.typography.titleMedium)
                EditorTestCase("Ends w/ Landscape (AR 1.5)", 1, 2, 3, 4, 0)
                EditorTestCase("Ends w/ Wide (AR 1.66)",     0, 2, 3, 4, 1)
                EditorTestCase("Ends w/ Panorama (AR 2.0)",  0, 1, 3, 4, 2)
                EditorTestCase("Ends w/ Portrait (AR 0.5)",  0, 1, 2, 4, 3)
                EditorTestCase("Ends w/ Square (AR 1.0)",    0, 1, 2, 3, 4)
            }

            Divider()

            // --------------------------------------------------
            // 4 FOTOĞRAF (Kalan 1 -> Son 1 resim en tepeye DEV olarak çıkar)
            // --------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("--- 4 IMAGES (Top Row: 1) ---", style = MaterialTheme.typography.titleMedium)
                EditorTestCase("Ends w/ Landscape", 1, 2, 3, 0)
                EditorTestCase("Ends w/ Wide",      0, 2, 4, 1)
                EditorTestCase("Ends w/ Panorama",  0, 1, 4, 2)
                EditorTestCase("Ends w/ Portrait",  0, 1, 4, 3) // <-- En kritik test! Dikey resim tepede.
                EditorTestCase("Ends w/ Square",    0, 1, 2, 4)
            }

            Divider()

            // --------------------------------------------------
            // 3 FOTOĞRAF (Kalan 0 -> Son 3 resim en tepeye 3'lü olarak çıkar)
            // --------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("--- 3 IMAGES (Top Row: 3) ---", style = MaterialTheme.typography.titleMedium)
                EditorTestCase("Ends w/ Landscape", 1, 2, 0)
                EditorTestCase("Ends w/ Wide",      0, 2, 1)
                EditorTestCase("Ends w/ Panorama",  0, 3, 2)
                EditorTestCase("Ends w/ Portrait",  1, 4, 3)
                EditorTestCase("Ends w/ Square",    2, 3, 4)
            }

            Divider()

            // --------------------------------------------------
            // 2 FOTOĞRAF (Kalan 2 -> 2 resim yan yana)
            // --------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("--- 2 IMAGES ---", style = MaterialTheme.typography.titleMedium)
                EditorTestCase("Ends w/ Landscape", 1, 0)
                EditorTestCase("Ends w/ Wide",      2, 1)
                EditorTestCase("Ends w/ Panorama",  3, 2)
                EditorTestCase("Ends w/ Portrait",  4, 3)
                EditorTestCase("Ends w/ Square",    0, 4)
            }

            Divider()

            // --------------------------------------------------
            // 1 FOTOĞRAF (Kalan 1 -> Tek resim)
            // --------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("--- 1 IMAGE ---", style = MaterialTheme.typography.titleMedium)
                EditorTestCase("Landscape", 0)
                EditorTestCase("Wide",      1)
                EditorTestCase("Panorama",  2)
                EditorTestCase("Portrait",  3)
                EditorTestCase("Square",    4)
            }
        }
    }
}