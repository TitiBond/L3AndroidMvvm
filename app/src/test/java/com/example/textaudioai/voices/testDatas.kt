package com.example.textaudioai.voices

import android.util.Log
import com.example.textaudioai.repositories.Player
import java.util.Date

val voicesMockup: List<Player> = listOf(
Player(1,"Bonjour", 1,"",5.0,"", Date(2024,11,20), Date() ),
Player(2,"coucou",1,"",6.0,"", Date(), Date() ),
Player(3,"Robert",1,"",7.5,"", Date(), Date() ),
Player(4,"Roberto",1,"",7.5,"", Date(2023), Date() ))

val resMockup: List<Player> = listOf(voicesMockup[0])

val sortAscResMockup: List<Player> = listOf(voicesMockup[3], voicesMockup[1], voicesMockup[2],
    voicesMockup[0])

val sortAscTextResMockup: List<Player> = listOf(voicesMockup[3], voicesMockup[2])


val errMock: Exception = java.lang.Exception("coucou")