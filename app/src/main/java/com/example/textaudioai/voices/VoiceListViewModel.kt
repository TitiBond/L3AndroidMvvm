package com.example.textaudioai.voices

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.R
import com.example.textaudioai.repositories.PaperPlayersRepository
import com.example.textaudioai.repositories.Player
import com.example.textaudioai.utils.DateFilteredType
import com.example.textaudioai.utils.filterVoices
import java.util.Date

sealed class VoiceListViewModelState{
    object Loading: VoiceListViewModelState()
    data class Full(val voices: List<Player>): VoiceListViewModelState()
    object Empty: VoiceListViewModelState()
    data class Error(val error: Exception): VoiceListViewModelState()
}


class VoiceListViewModel: ViewModel() {

    lateinit var repository: PaperPlayersRepository
    private var voices: List<Player> = listOf()
    private var filteredVoices: List<Player> = listOf()
    val state = MutableLiveData<VoiceListViewModelState>()
    private var filterText: String = ""
    private var dateFilterType: DateFilteredType = DateFilteredType.NONE

    // TODO LINES TO DELETE WHEN ADD VOICE ITEM DONE
 /*private val voicesMockup = listOf(
        Player(1,"Bonjour", R.drawable.ic_launcher_background,"",5.0,"", Date(2024,11,20), Date() ),
        Player(2,"coucou",R.drawable.ic_launcher_background,"",6.0,"", Date(), Date() ),
        Player(3,"Robert",R.drawable.ic_launcher_background,"",7.5,"", Date(), Date() ),
        Player(4,"Roberto",R.drawable.ic_launcher_background,"",7.5,"", Date(2023), Date() ),
    )*/
    fun loadVoices(){
        state.value = VoiceListViewModelState.Loading
        try {
            voices = repository.findAllPlayers()
            filteredVoices = voices
            if (voices.isEmpty()){
                state.value = VoiceListViewModelState.Empty
            }else{
                state.value = VoiceListViewModelState.Full(voices)
            }
        }catch (err: Exception){
            state.value = VoiceListViewModelState.Error(err)
        }
    }

    fun updateFilterText(text: String){
        filterText = text.lowercase()
        if(filterText.isEmpty()) {
            loadVoices()
        }
        filter()
    }

    fun updateDateFilter(value:DateFilteredType){
        dateFilterType = value
        filter()
    }

    fun filter(){
        filteredVoices = filterVoices(voices,filterText,dateFilterType)
        state.value = VoiceListViewModelState.Full(filteredVoices)
    }
}


