package com.example.textaudioai.voices

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.R
import io.paperdb.Paper
import java.util.Date

sealed class VoiceListViewModelState{
    class Loading(): VoiceListViewModelState(){
        override fun equals(other: Any?): Boolean {
            return if(other !== null){
                this.javaClass === other.javaClass
            }else{
                false
            }
        }
    }
    data class Full(val voices: List<Voice>): VoiceListViewModelState()
    class Empty(): VoiceListViewModelState(){
        override fun equals(other: Any?): Boolean {
            return if(other !== null){
                this.javaClass === other.javaClass
            }else{
                false
            }
        }
    }
    class Error(): VoiceListViewModelState()
}
class VoiceListViewModel: ViewModel() {

    lateinit var db: LocalDataBase
    private var voices: List<Voice> = listOf()

    // TODO LINES TO DELETE WHEN ADD VOICE ITEM DONE
/*  private val voicesMockup = listOf(
        Voice(1,"title1", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(2,"title2", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(3,"title3", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(4,"title4", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(5,"title5", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(6,"title6", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(7,"title7", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(8,"title8", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(9,"title9", Date(), 200.0, R.drawable.ic_launcher_background),
        Voice(10,"title10", Date(), 200.0, R.drawable.ic_launcher_background),
    )*/

    val state = MutableLiveData<VoiceListViewModelState>()

    fun loadVoices(){
        state.value = VoiceListViewModelState.Loading()
        voices = db.getVoices()
        if (voices.isEmpty()){
            state.value = VoiceListViewModelState.Empty()
        }else{
            state.value = VoiceListViewModelState.Full(voices)
        }
    }
}
