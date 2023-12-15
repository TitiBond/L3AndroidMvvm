package com.example.textaudioai.voices

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.textaudioai.repositories.PaperPlayersRepository
import com.example.textaudioai.testObserver
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
        val observer = model.state.testObserver()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()

        //ASSERT
        Assert.assertEquals(
            listOf(
                VoiceListViewModelState.Loading,
                VoiceListViewModelState.Full(voicesMockup)
            ),
            observer.observedValues
        )
    }

    @Test
    fun `load voices should yields state Empty`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val observer = model.state.testObserver()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(listOf())

        //ACT
        model.loadVoices()

        //ASSERT
        Assert.assertEquals(
            listOf(
                VoiceListViewModelState.Loading,
                VoiceListViewModelState.Empty
            ),
            observer.observedValues
        )
    }

    @Test
    fun `load voices should yields state Error`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val observer = model.state.testObserver()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenThrow(errMock)

        //ACT
        model.loadVoices()

        //ASSERT
        Assert.assertEquals(
            listOf(
                VoiceListViewModelState.Loading,
                VoiceListViewModelState.Error(errMock)
            ),
            observer.observedValues
        )
    }
}