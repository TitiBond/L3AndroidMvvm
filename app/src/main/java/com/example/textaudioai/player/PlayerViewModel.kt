package com.example.textaudioai.player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.player.media.MediaPlayerCustom

sealed class PlayerViewState {
    object Loading : PlayerViewState();
    data class Success(val player: Player): PlayerViewState();
    data class Error(val message: Int): PlayerViewState();
}

sealed class MediaPlayerState {
    object Idle: MediaPlayerState();
    object Started : MediaPlayerState();

    object Stopped: MediaPlayerState();
}


class PlayerViewModel(): ViewModel(), MediaPlayerCustom.Listener {
    // TODO: This view model require an access to the database for fetching the player from the id

    lateinit var player: MediaPlayerCustom;
    val playerStateLiveData = MutableLiveData<PlayerViewState>(PlayerViewState.Loading);
    val mediaPlayerStateLiveData = MutableLiveData<MediaPlayerState>(MediaPlayerState.Idle);

    fun loadPlayer(id: Int) {
        // TODO: Add a BDD call with Pepper and retrieve the player from the id
        val player = Player(id, "Random title", "/to/file/path", "lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum ", "2023");

        playerStateLiveData.value = PlayerViewState.Success(player);
    }

    fun loadMediaPlayer(filePath: String) {
        player.loadFile(filePath);
    }

    fun handlePlaybackState() {
        if (player.isPlaying()) {
            stopPlayback(false);
        } else {
            startPlayback();
        }
    }

    private fun startPlayback() {
        player.start();
        mediaPlayerStateLiveData.value = MediaPlayerState.Started;
    }

    fun stopPlayback(rewind: Boolean) {
        player.stop(rewind);
        mediaPlayerStateLiveData.value = MediaPlayerState.Stopped;
    }

    fun rewindPlayback() {
        stopPlayback(true); // Stop the playback and rewind
        mediaPlayerStateLiveData.value = MediaPlayerState.Idle;
    }

    override fun onLoadedSuccess() {
        Log.i("PlayerViewModel", "onLoadedSuccess: media loaded");
    }

    override fun onLoadedError(message: Int) {
        playerStateLiveData.value = PlayerViewState.Error(message);
    }
}