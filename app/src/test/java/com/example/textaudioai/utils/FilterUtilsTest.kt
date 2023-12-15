package com.example.textaudioai.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.textaudioai.repositories.Player
import com.example.textaudioai.voices.resMockup
import com.example.textaudioai.voices.voicesMockup
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class FilterUtilsTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `basic text search with bo should return bonjour Player`(){
        //ARRANGE
        val filterText = "bo"
        val dateFilterType = DateFilteredType.NONE

        //ACT
         var voicesResult = filterVoices(voicesMockup,filterText,dateFilterType)

        //ASSERT
        Assert.assertEquals(
            resMockup,
            voicesResult
        )
    }
}