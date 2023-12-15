package com.example.textaudioai.voices

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.textaudioai.databinding.ItemVoiceListBinding
import com.example.textaudioai.player.PlayerActivity
import com.example.textaudioai.repositories.Player
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.logging.SimpleFormatter

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
            voiceItemImageView.setImageBitmap(BitmapFactory.decodeFile(voice.image))
            voiceItemTitleTextView.text = voice.title
            voiceItemContentTextView.text = voice.content
            voiceITemDatetextView.text = SimpleDateFormat("dd/MM/yy").format(voice.updatedAt)
            voiceItem.setOnClickListener {
                val context = it.context
                val intent = Intent(context,PlayerActivity::class.java)
                intent.putExtra("playerId", voice.id)
                context.startActivity(intent)
            }
        }
    }
}