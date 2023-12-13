package com.example.textaudioai.player.media

import android.media.MediaPlayer
import com.example.textaudioai.R

interface MediaPlayerCustom {
    val listener: Listener

    interface Listener {
        fun onLoadedSuccess()
        fun onLoadedError()
    }

    fun loadFile(filePath: String);
    fun isPlaying(): Boolean;
    fun start();
    fun stop(rewind: Boolean)
}

class MediaPlayerWrapper(private val player: MediaPlayer,
                         override val listener: MediaPlayerCustom.Listener
): MediaPlayerCustom {
    override fun loadFile(filePath: String) {
        player.apply {
            setDataSource(filePath)
            prepareAsync()
            setOnPreparedListener {
                listener.onLoadedSuccess();
            }
            setOnErrorListener { _, _, _ ->
                listener.onLoadedError();
                return@setOnErrorListener false
            }
        }
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying;
    }

    override fun start() {
        player.start();
    }

    override fun stop(rewind: Boolean) {
        player.apply {
            if (isPlaying) {
                pause()
            }

            if (rewind) {
                seekTo(0)
            }
        }
    }
}