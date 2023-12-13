package com.example.textaudioai.voices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.textaudioai.databinding.ActivityVoiceListBinding
import com.example.textaudioai.camera.CameraActivity
import io.paperdb.Paper


class VoiceListActivity: AppCompatActivity() {

    private val viewModel: VoiceListViewModel by viewModels()
    private lateinit var binding: ActivityVoiceListBinding
    private lateinit var adapter: VoiceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(this);
        binding = ActivityVoiceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CLICK ON ADD VOICE BUTTON
        binding.addVoiceButton.setOnClickListener{
            intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.refreshButton.setOnClickListener {
            viewModel.loadVoices()
        }

        viewModel.state.observe(this){ state ->
            when(state){
                is VoiceListViewModelState.Empty -> {
                    binding.emptyListTextView.visibility = View.VISIBLE
                    binding.emptyListTextView.setText(state.message)
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.GONE
                }
                is VoiceListViewModelState.Full -> {
                    adapter.voices = state.voices
                    binding.emptyListTextView.visibility = View.GONE
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.GONE
                }
                is VoiceListViewModelState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    binding.refreshButton.visibility = View.VISIBLE
                    binding.emptyListTextView.setText(state.message)
                    binding.emptyListTextView.visibility = View.VISIBLE
                    binding.loadingProgressBar.visibility = View.GONE
                }
                is VoiceListViewModelState.Loading -> {
                    binding.emptyListTextView.setText(state.message)
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.VISIBLE
                }
            }
        }

        adapter = VoiceListAdapter(listOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadVoices()
    }
}