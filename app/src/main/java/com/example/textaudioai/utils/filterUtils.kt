package com.example.textaudioai.utils

import com.example.textaudioai.repositories.Player

enum class DateFilteredType{
    NONE,
    ASC,
    DESC,

}
fun filterVoices(voices: List<Player>, text:String, filterType:DateFilteredType): List<Player>{
    var filteredVoices: List<Player> = voices.filter { it.title.lowercase().contains(text) }
     when(filterType){
         DateFilteredType.NONE -> return filteredVoices.sortedBy { it.id }
         DateFilteredType.ASC -> return filteredVoices.sortedBy { it.updatedAt }
         DateFilteredType.DESC -> return filteredVoices.sortedByDescending { it.updatedAt }
     }
}