package com.example.textaudioai.voices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityVoiceListBinding
import com.example.textaudioai.camera.CameraActivity
import com.example.textaudioai.repositories.PlayersRepository
import io.paperdb.Paper

private const val TAG = "VoicesViewModel";
class VoiceListActivity: AppCompatActivity() {

    private val viewModel: VoiceListViewModel by viewModels()
    private lateinit var binding: ActivityVoiceListBinding
    private lateinit var adapter: VoiceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(this);
        viewModel.repository = PlayersRepository()

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

        binding.filterEditText.addTextChangedListener{
            viewModel.updateFilterText(it.toString())
            adapter.notifyDataSetChanged()
        }

        viewModel.state.observe(this){ state ->
            when(state){
                is VoiceListViewModelState.Empty -> {
                    binding.emptyListTextView.visibility = View.VISIBLE
                    binding.emptyListTextView.setText(R.string.voice_list_add_voice)
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.filterEditText.visibility = View.GONE
                }
                is VoiceListViewModelState.Full -> {
                    adapter.voices = state.voices
                    binding.emptyListTextView.visibility = View.GONE
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.filterEditText.visibility = View.VISIBLE
                }
                is VoiceListViewModelState.Error -> {
                    Toast.makeText(this, R.string.voice_list_error_state, Toast.LENGTH_SHORT).show()
                    binding.refreshButton.visibility = View.VISIBLE
                    binding.emptyListTextView.setText(R.string.voice_list_error_state)
                    binding.emptyListTextView.visibility = View.VISIBLE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.filterEditText.visibility = View.GONE
                    Log.i(TAG,getString(R.string.voice_list_error_logi),state.error)
                }
                is VoiceListViewModelState.Loading -> {
                    binding.emptyListTextView.setText(R.string.voice_list_loading_state)
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    binding.filterEditText.visibility = View.GONE
                }
            }
        }

        adapter = VoiceListAdapter(listOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadVoices()
    }
}