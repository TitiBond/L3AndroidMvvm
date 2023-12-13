package com.example.textaudioai.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding;
    private val viewModel: PlayerViewModel by viewModels();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val playerId = intent.getIntExtra("playerId", -1);

        viewModel.stateLiveData.observe(this) {
            updateUI(it);
        }

        viewModel.loadPlayer(playerId);
    }

    private fun updateUI(state: PlayerViewState) {
        when(state) {
            is PlayerViewState.Loading -> {
                // TODO: Add a better loading, perhaps skeleton loader. How to ?
                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            }
            is PlayerViewState.Success -> {
                // TODO: Add the audioUrl to the player for stream;
                binding.updatedAtTextView.text = state.player.updatedAt;
                binding.titleTextView.text = state.player.title;
                binding.contentTextView.text = state.player.content;
            }
            is PlayerViewState.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}