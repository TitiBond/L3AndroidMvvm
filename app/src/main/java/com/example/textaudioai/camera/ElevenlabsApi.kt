package com.example.textaudioai.camera

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class TTSBodyVoiceSettings(val stability: Double, val similarity_boost: Double)
data class TTSBody(val text: String, val model_id: String, val voice_settings: TTSBodyVoiceSettings)

interface ElevenlabsApi {
    @POST("v1/text-to-speech/21m00Tcm4TlvDq8ikWAM")
    fun convertTTS(
        @Header("Accept") mimeType: String,
        @Header("Content-Type") contentType: String,
        @Header("xi-api-key") apiKey: String,
        @Body() body: TTSBody
    ): Call<ResponseBody>
}
