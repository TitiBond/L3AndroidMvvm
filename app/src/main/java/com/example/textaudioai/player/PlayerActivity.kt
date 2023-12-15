package com.example.textaudioai.player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityPlayerBinding
import com.example.textaudioai.player.media.MediaPlayerWrapper
import com.example.textaudioai.repositories.PlayersRepository
import io.paperdb.Paper

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding;
    private val viewModel: PlayerViewModel by viewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        viewModel.repository = PlayersRepository();

        binding = ActivityPlayerBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val playerId = intent.getIntExtra("playerId", -1);
        val backButton = binding.include.backButton;
        val deleteButton = binding.include.deleteButton;

        val player = MediaPlayerWrapper(MediaPlayer(), viewModel);
        viewModel.player = player;

        backButton.setOnClickListener {
            finish();
        }

        deleteButton.setOnClickListener {
            val success = viewModel.removePlayer(playerId);
            if (success) {
                finish()
            } else {
                Toast.makeText(this, "Failed to remove the player", Toast.LENGTH_SHORT).show();
            }
        }

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

                binding.updatedAtTextView.text = state.player.updatedAt.toString();
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
                binding.playerRewindimageButton.visibility = View.GONE;
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
