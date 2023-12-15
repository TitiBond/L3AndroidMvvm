package com.example.textaudioai.voices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityVoiceListBinding
import com.example.textaudioai.camera.CameraActivity
import com.example.textaudioai.repositories.PlayersRepository
import com.example.textaudioai.utils.DateFilteredType
import io.paperdb.Paper

private const val TAG = "VoicesViewModel";
class VoiceListActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {

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

        //ERROR CASE CLICK ON RETRY BUTTON
        binding.refreshButton.setOnClickListener {
            viewModel.loadVoices()
        }

        //WRITE IN FILTER TEXT INPUT
        binding.filterEditText.addTextChangedListener{
            viewModel.updateFilterText(it.toString())
            adapter.notifyDataSetChanged()
        }

        //ON SELECT DROPDOWN DATE FILTER ITEM
        binding.dateFilterSpinner.onItemSelectedListener = this


        viewModel.state.observe(this){ state ->
            when(state){
                is VoiceListViewModelState.Empty -> {
                    adapter.voices = listOf();
                    binding.emptyListTextView.visibility = View.VISIBLE
                    binding.emptyListTextView.setText(R.string.voice_list_add_voice)
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.filterEditText.visibility = View.GONE
                    binding.dateFilterSpinner.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }
                is VoiceListViewModelState.Full -> {
                    adapter.voices = state.voices
                    binding.emptyListTextView.visibility = View.GONE
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.filterEditText.visibility = View.VISIBLE
                    binding.dateFilterSpinner.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                }
                is VoiceListViewModelState.Error -> {
                    Toast.makeText(this, R.string.voice_list_error_state, Toast.LENGTH_SHORT).show()
                    binding.refreshButton.visibility = View.VISIBLE
                    binding.emptyListTextView.setText(R.string.voice_list_error_state)
                    binding.emptyListTextView.visibility = View.VISIBLE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.filterEditText.visibility = View.GONE
                    binding.dateFilterSpinner.visibility = View.GONE
                    Log.i(TAG,getString(R.string.voice_list_error_logi),state.error)

                }
                is VoiceListViewModelState.Loading -> {
                    binding.emptyListTextView.setText(R.string.voice_list_loading_state)
                    binding.refreshButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    binding.filterEditText.visibility = View.GONE
                    binding.dateFilterSpinner.visibility = View.GONE
                }
            }
        }

        adapter = VoiceListAdapter(listOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.loadVoices()

        //DROPDOWN MENU
        ArrayAdapter.createFromResource(
            this,
            R.array.voice_list_sort_filter_texts,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            binding.dateFilterSpinner.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadVoices()
    }

    //DROPDOWN MENU ON SELECT METHOD
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val f = when(pos){
            0->  DateFilteredType.NONE
            1-> DateFilteredType.ASC
            2-> DateFilteredType.DESC
            else -> DateFilteredType.NONE
        }
        viewModel.updateDateFilter(f)
        adapter.notifyDataSetChanged()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}