package com.example.textaudioai.voices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityVoiceListBinding
import com.example.textaudioai.player.PlayerActivity

class VoiceListActivity : AppCompatActivity() {

    private val viewModel: VoiceListViewModel by viewModels()
    private lateinit var binding: ActivityVoiceListBinding
    private lateinit var adapter: VoiceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_list)

        binding = ActivityVoiceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.state.observe(this){ state ->
            when(state){
                is VoiceListViewModelState.Empty -> {
                    binding.emptyListTextView.setText(state.message)
                }
                is VoiceListViewModelState.Full -> {
                    adapter.voices = state.voices
                }
            }
        }

        adapter = VoiceListAdapter(listOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadVoices()


    }
}