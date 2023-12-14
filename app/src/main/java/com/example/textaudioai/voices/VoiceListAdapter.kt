package com.example.textaudioai.voices

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.textaudioai.databinding.ItemVoiceListBinding
import com.example.textaudioai.player.PlayerActivity
import com.example.textaudioai.repositories.Player

class VoiceListAdapter(var voices: List<Player>): RecyclerView.Adapter<VoiceListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemVoiceListBinding): RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVoiceListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return voices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voice = voices[position]

        with(holder.binding){
            voiceItemImageView.setImageResource(voice.image)
            voiceItemTitleTextView.text = voice.title
            voiceITemDurationtextView.text = "${voice.duration} secondes"
            voiceItem.setOnClickListener {
                val context = it.context
                val intent = Intent(context,PlayerActivity::class.java)
                intent.putExtra("playerId", voice.id)
                context.startActivity(intent)
            }
        }
    }
}