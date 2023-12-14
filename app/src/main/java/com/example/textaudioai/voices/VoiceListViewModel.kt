package com.example.textaudioai.voices

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.R
import com.example.textaudioai.repositories.PaperPlayersRepository
import com.example.textaudioai.repositories.Player
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
    val state = MutableLiveData<VoiceListViewModelState>()
    private var filterText: String = ""

    // TODO LINES TO DELETE WHEN ADD VOICE ITEM DONE
 /*private val voicesMockup = listOf(
        Player(1,"Bonjour", R.drawable.ic_launcher_background,"",5.0,"", Date(), Date() ),
        Player(2,"coucou",R.drawable.ic_launcher_background,"",6.0,"", Date(), Date() ),
        Player(3,"Robert",R.drawable.ic_launcher_background,"",7.5,"", Date(), Date() ),
    )*/
    fun loadVoices(){
        state.value = VoiceListViewModelState.Loading
        try {
            voices = repository.findAllPlayers()
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
        if (filterText.length >= 3){
            filterByTitle()
        }else{
            loadVoices()
        }
    }

    fun filterByTitle(){
        try {
            var filteredPlayers = repository.findManyByTitle(filterText)
            state.value = VoiceListViewModelState.Full(filteredPlayers)
        }catch (err: Exception){
            state.value = VoiceListViewModelState.Error(err)
        }

    }
}
