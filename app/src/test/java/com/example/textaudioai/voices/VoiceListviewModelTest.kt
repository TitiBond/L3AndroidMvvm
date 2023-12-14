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

    @Test
    fun `filter voices with text bon should return bonjour Player`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateFilterText("bon")

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(resMockup),
            model.state.value
        )
    }

    @Test
    fun `filter voices with text chocolat should return empty list`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateFilterText("chocolat")

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(listOf()),
            model.state.value
        )
    }

    @Test
    fun `filter voices with text ch should return normal list`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateFilterText("ch")

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(voicesMockup),
            model.state.value
        )
    }

    @Test
    fun `filter voices with date sorting ASC should return acsending sorted list`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateDateFilter(1)

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(sortAscResMockup),
            model.state.value
        )
    }

    @Test
    fun `filter voices with date sorting DESC should return descending sorted list`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateDateFilter(2)

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(voicesMockup),
            model.state.value
        )
    }

    @Test
    fun `filter voices with date sorting ASC and text robe should return descending sorted list`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateDateFilter(1)
        model.updateFilterText("robe")

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(sortAscTextResMockup),
            model.state.value
        )
    }

    @Test
    fun `filter voices with date sorting ASC and text robe then notext should return descending sorted list`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateDateFilter(1)
        model.updateFilterText("robe")
        model.updateFilterText("")

        //ASSERT
        Assert.assertEquals(
            VoiceListViewModelState.Full(sortAscResMockup),
            model.state.value
        )
    }

    @Test
    fun `filter final test should return normal list`(){
        //ARRANGE
        val model = VoiceListViewModel()
        val db = mock<PaperPlayersRepository>()
        val observer = model.state.testObserver()
        model.repository = db
        whenever(db.findAllPlayers()).thenReturn(voicesMockup)

        //ACT
        model.loadVoices()
        model.updateDateFilter(1)
        model.updateFilterText("robe")
        model.updateFilterText("")
        model.updateDateFilter(2)
        model.updateDateFilter(0)
        model.updateFilterText("zozo")

        //ASSERT
        Assert.assertEquals(
            listOf(
                VoiceListViewModelState.Loading,
                VoiceListViewModelState.Full(voicesMockup),
                VoiceListViewModelState.Full(sortAscResMockup),
                VoiceListViewModelState.Full(sortAscTextResMockup),
                VoiceListViewModelState.Full(sortAscResMockup),
                VoiceListViewModelState.Full(voicesMockup),
                VoiceListViewModelState.Full(voicesMockup),
                VoiceListViewModelState.Full(listOf())
            ),
            observer.observedValues
        )
    }
}