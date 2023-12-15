package com.example.textaudioai.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.textaudioai.repositories.Player
import com.example.textaudioai.voices.resMockup
import com.example.textaudioai.voices.sortAscResMockup
import com.example.textaudioai.voices.sortAscTextResMockup
import com.example.textaudioai.voices.sortTextRobeResMockup
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

    @Test
    fun `basic text search with robe should return robert and roberto Player`(){
        //ARRANGE
        val filterText = "robe"
        val dateFilterType = DateFilteredType.NONE

        //ACT
        var voicesResult = filterVoices(voicesMockup,filterText,dateFilterType)

        //ASSERT
        Assert.assertEquals(
            sortTextRobeResMockup,
            voicesResult
        )
    }

    @Test
    fun `basic text search with robe and ASC date filter should return roberto and robert Player`(){
        //ARRANGE
        val filterText = "robe"
        val dateFilterType = DateFilteredType.ASC

        //ACT
        var voicesResult = filterVoices(voicesMockup,filterText,dateFilterType)

        //ASSERT
        Assert.assertEquals(
            sortAscTextResMockup,
            voicesResult
        )
    }

    @Test
    fun `basic text search with zoubi should return empty list`(){
        //ARRANGE
        val filterText = "zoubi"
        val dateFilterType = DateFilteredType.ASC

        //ACT
        var voicesResult = filterVoices(voicesMockup,filterText,dateFilterType)

        //ASSERT
        Assert.assertEquals(
            listOf<Player>() ,
            voicesResult
        )
    }

    @Test
    fun `ASC date filter return a sorted list`(){
        //ARRANGE
        val filterText = ""
        val dateFilterType = DateFilteredType.ASC

        //ACT
        var voicesResult = filterVoices(voicesMockup,filterText,dateFilterType)

        //ASSERT
        Assert.assertEquals(
            sortAscResMockup,
            voicesResult
        )
    }
}