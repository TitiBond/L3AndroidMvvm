package com.example.textaudioai.voices

import java.util.Date

@Deprecated("Use Player data class from the package repository instead")
data class Voice(
    val id: Int,
    val title:String,
    val date: Date,
    val duration: Double,
    val image: Int
)
