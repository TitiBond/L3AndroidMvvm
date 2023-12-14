package com.example.textaudioai.voices

import io.paperdb.Paper

interface LocalDataBase {
    fun getVoices(): List<Voice>
    fun saveVoices(voices: List<Voice>)
}

@Deprecated("Use PlayersRepository from the package repository instead")
class PaperDb: LocalDataBase{
    override fun getVoices(): List<Voice> {
        return Paper.book().read("voices") ?: listOf()
    }

    override fun saveVoices(voices: List<Voice>) {
        TODO("Not yet implemented")
    }
}