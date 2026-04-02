package com.mustafakoceerr.imageexample

import androidx.annotation.DrawableRes

/**
 * Core data models for the Note application.
 * Contains definitions for individual images and complete note entities.
 */

data class NoteImage(
    @DrawableRes val drawableRes: Int,
    val aspectRatio: Float
)

data class NoteData(
    val id: Int,
    val images: List<NoteImage> = emptyList(),
    val title: String = "",
    val content: String = "",
    val hasSound: Boolean = false
)