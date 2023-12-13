package com.example.textaudioai.voices

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.R
import com.example.textaudioai.models.Voice
import java.util.Date

sealed class VoiceListViewModelState{
    data class Full(val voices: List<Voice>): VoiceListViewModelState()
    data class Empty(val message: Int): VoiceListViewModelState()
}
class VoiceListViewModel: ViewModel() {

    //mock de données
    private val voices = listOf(
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
    )
    val state = MutableLiveData<VoiceListViewModelState>()


    fun loadVoices(){
        //on suppose check sur db simuler avec la liste de voice
        if (voices.size <= 0){
            state.value = VoiceListViewModelState.Empty(R.string.voice_list_add_voice)
        }else{
            state.value = VoiceListViewModelState.Full(voices)
        }
    }


}
