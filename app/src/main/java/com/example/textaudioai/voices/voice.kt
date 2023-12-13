package com.example.textaudioai.voices

import java.util.Date

data class Voice(
    val id: Int,
    val title:String,
    val date: Date,
    val duration: Double,
    val image : Int
)
