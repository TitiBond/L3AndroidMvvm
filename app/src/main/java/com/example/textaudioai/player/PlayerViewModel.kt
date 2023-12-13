package com.example.textaudioai.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

sealed class PlayerViewState {
    object Loading : PlayerViewState();
    data class Success(val player: Player): PlayerViewState();
    data class Error(val message: String): PlayerViewState();
}

class PlayerViewModel: ViewModel() {
    // TODO: This view model require an access to the database for fetching the player from the id
    val stateLiveData = MutableLiveData<PlayerViewState>();

    fun loadPlayer(id: Int) {
        stateLiveData.value = PlayerViewState.Loading;

        // TODO: Add a BDD call with Pepper and retrieve the player from the id
        val player = Player(id, "Random title", "http://smt", "lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum ", "2023");

        stateLiveData.value = PlayerViewState.Success(player);
    }

    // TODO: Player management play/pause/stream from player.filePath
}