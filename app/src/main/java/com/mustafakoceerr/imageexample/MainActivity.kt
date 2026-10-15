package com.mustafakoceerr.imageexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mustafakoceerr.imageexample.ui.DemoApp
import com.mustafakoceerr.imageexample.ui.theme.ImageExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ImageExampleTheme {
                DemoApp()
            }
        }
    }
}
