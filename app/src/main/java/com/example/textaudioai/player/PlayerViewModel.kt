package com.example.textaudioai.player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.player.media.MediaPlayerCustom
import com.example.textaudioai.repositories.Player
import com.example.textaudioai.repositories.PlayerRepository

sealed class PlayerViewState {
    object Loading : PlayerViewState();
    data class Success(val player: Player): PlayerViewState();
    object Error: PlayerViewState();
}

sealed class MediaPlayerState {
    object Idle: MediaPlayerState();
    object Started : MediaPlayerState();

    object Stopped: MediaPlayerState();
}


private const val TAG = "PlayerViewModel";

class PlayerViewModel(): ViewModel(), MediaPlayerCustom.Listener {
    lateinit var player: MediaPlayerCustom;
    lateinit var repository: PlayerRepository;
    val playerStateLiveData = MutableLiveData<PlayerViewState>(PlayerViewState.Loading);
    val mediaPlayerStateLiveData = MutableLiveData<MediaPlayerState>(MediaPlayerState.Idle);

    fun loadPlayer(id: Int) {
        try {
            val player = repository.findOnePlayer(id);
            if (player == null) {
                Log.i(TAG, "loadPlayer: Player not found from id $id");
                playerStateLiveData.value = PlayerViewState.Error;
                return;
            }

            playerStateLiveData.value = PlayerViewState.Success(player);
        } catch (err: Exception) {
            Log.i(TAG, "loadPlayer: Failed to retrieve player", err);
            playerStateLiveData.value = PlayerViewState.Error;
        }
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

    override fun onLoadedError() {
        playerStateLiveData.value = PlayerViewState.Error
    }
}