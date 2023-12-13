package com.example.textaudioai.voices

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class VoiceListviewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `load voices should yields state Full`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<LocalDataBase>()
        model.db = db
        whenever(db.getVoices()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(voicesMockup),
            model.state.value
        )
    }

    @Test
    fun `load voices should yields state Empty`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<LocalDataBase>()
        model.db = db
        whenever(db.getVoices()).thenReturn(listOf())

        //ACT
        model.loadVoices()

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Empty(),
            model.state.value
        )
    }
}