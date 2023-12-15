package com.example.textaudioai.camera

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

data class TextItem(
    val text: String,
)

interface NinjasApi {
    @Multipart
    @POST("v1/imagetotext")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Header("X-Api-Key") apiKey: String
    ): Call<List<TextItem>>
}
