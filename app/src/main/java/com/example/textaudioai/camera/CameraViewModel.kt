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
    data class OCRSuccess(val text: String): CameraViewModelState()
    data class PromptRejected(val message: String): CameraViewModelState()
    data class PromptValidated(val text: String): CameraViewModelState()
    data class Saved(val a: Any): CameraViewModelState()
}
class CameraViewModel: ViewModel() {

    val imagePath = MutableLiveData<String>()
    lateinit var api: NinjasApi

    private val state = MutableLiveData<CameraViewModelState>()
    val formattedText = MutableLiveData<String>()

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
                println("TEXT API RESPONSE :")
                println(response.body())
                state.value = CameraViewModelState.OCRSuccess("success")
                response.body()?.let { formatText(it) }
            }

            override fun onFailure(call: Call<List<TextItem>>, t: Throwable) {
                println(t)
                state.value = CameraViewModelState.OCRSuccess("error in text formatting")
            }

        })

    }

    private fun validatePrompt() {

    }

    private fun rejectPrompt() {

    }

    private fun formatText(data: List<TextItem>) {
        val formattedString = data.joinToString(separator = " ") { it.text }
        formattedText.postValue(formattedString)
        println("FORMATTED TEXT")
        println(formattedText)
    }

}