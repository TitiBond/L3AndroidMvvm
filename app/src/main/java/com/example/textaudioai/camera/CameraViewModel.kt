package com.example.textaudioai.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
sealed class CameraViewModelState {

    data class Loading(val message: String): CameraViewModelState()
    data class OCRError(val message: String): CameraViewModelState()
    data class OCRSuccess(val message: String): CameraViewModelState()
    data class PromptRejected(val text: String): CameraViewModelState()
    data class PromptValidated(val text: String): CameraViewModelState()
    data class Saved(val a: Any): CameraViewModelState()
}
class CameraViewModel: ViewModel() {

    val imagePath = MutableLiveData<String>()
    lateinit var api: NinjasApi

    private val state = MutableLiveData<CameraViewModelState>()

    fun setImagePath(path: String) {
        imagePath.value = path
    }
    fun analysePicture(imageFile: File) {
        state.value = CameraViewModelState.Loading("Processing...")

        // CALL API
        val part = MultipartBody.Part
            .createFormData(
                "image",
                imageFile.name,
                RequestBody.create(MediaType.parse("image/*"), imageFile))
        val call = api.uploadImage(part, "EtVLP/4mR4f5MZHhQba0rA==YJCYfIwmCvWErpE1")
        call.enqueue(object: Callback<List<TextItem>> {
            override fun onResponse(
                call: Call<List<TextItem>>,
                response: Response<List<TextItem>>
            ) {
                println(response.body())
            }

            override fun onFailure(call: Call<List<TextItem>>, t: Throwable) {
                println(t)
            }

        })

    }

    fun validatePrompt() {

    }

    fun rejectPrompt() {

    }
}