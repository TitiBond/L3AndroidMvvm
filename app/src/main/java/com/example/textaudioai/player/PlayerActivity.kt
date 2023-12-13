package com.example.textaudioai.player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityPlayerBinding
import com.example.textaudioai.player.media.MediaPlayerWrapper

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding;
    private val viewModel: PlayerViewModel by viewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val playerId = intent.getIntExtra("playerId", -1);

        val player = MediaPlayerWrapper(MediaPlayer(), viewModel);
        viewModel.player = player;

        viewModel.playerStateLiveData.observe(this) {
            updateUI(it);
        }

        viewModel.mediaPlayerStateLiveData.observe(this) {
            updatePlayerButtonUI(it);
        }

        viewModel.loadPlayer(playerId);
    }

    override fun onDestroy() {
        viewModel.stopPlayback(false);
        super.onDestroy();
    }

    private fun updateUI(state: PlayerViewState) {
        when (state) {
            is PlayerViewState.Loading -> {
                // TODO: Add a better loading, perhaps skeleton loader. How to ?
                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            }

            is PlayerViewState.Success -> {
                binding.playerImageButton.setOnClickListener {
                    viewModel.handlePlaybackState();
                }

                binding.playerRewindimageButton.setOnClickListener {
                    viewModel.rewindPlayback();
                }

                binding.updatedAtTextView.text = state.player.updatedAt;
                binding.titleTextView.text = state.player.title;
                binding.contentTextView.text = state.player.content;
                viewModel.loadMediaPlayer(state.player.filePath);
            }

            is PlayerViewState.Error -> {
                Toast.makeText(this, R.string.player_media_failed_file_load, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private fun updatePlayerButtonUI(state: MediaPlayerState) {
        when (state) {
            is MediaPlayerState.Idle -> {
                binding.playerRewindimageButton.visibility = View.INVISIBLE;
                binding.playerImageButton.setImageResource(android.R.drawable.ic_media_play);
            }
            is MediaPlayerState.Started -> {
                binding.playerRewindimageButton.visibility = View.VISIBLE;
                binding.playerImageButton.setImageResource(android.R.drawable.ic_media_pause);
            }
            is MediaPlayerState.Stopped -> {
                binding.playerImageButton.setImageResource(android.R.drawable.ic_media_play);
            }
        }
    }
}
