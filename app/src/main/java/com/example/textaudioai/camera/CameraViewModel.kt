package com.example.textaudioai.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.repositories.PaperPlayersRepository
import com.example.textaudioai.repositories.Player
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Date

sealed class CameraViewModelState {

    data class Loading(val message: String): CameraViewModelState()
    data class OCRError(val message: String): CameraViewModelState()
    data class OCRSuccess(val text: String): CameraViewModelState()
    data class PromptRejected(val message: String): CameraViewModelState()
    data class PromptValidated(val text: String): CameraViewModelState()
    data class Saved(val a: Any): CameraViewModelState()
}
class CameraViewModel  : ViewModel() {

    lateinit var repository: PaperPlayersRepository
    val imagePath = MutableLiveData<String>()
    lateinit var api: NinjasApi
    private lateinit var imageFile: File


    val state = MutableLiveData<CameraViewModelState>()

    fun createImageFile(context: Context): Uri {
        imageFile = File(context.filesDir, "picture-${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "com.example.android.fileprovider", imageFile)
    }

    private fun setImagePath(path: String) {
        imagePath.value = path
    }
    private fun analysePicture(imageFile: File) {
        state.value = CameraViewModelState.Loading("Processing...")

        // CALL API
        val part = MultipartBody.Part
            .createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
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

    fun handleActivityResult(resultCode: Int, data: Intent?, fileProvider: (Uri) -> File) {
        if (resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                val imageFile = fileProvider(imageUri)
                analysePicture(imageFile)
                setImagePath(imageFile.absolutePath)
            } else {
                imageFile.let { file ->
                    analysePicture(file)
                    setImagePath(file.absolutePath)
                }
            }
        } else {
            state.value = CameraViewModelState.OCRError("Action cancelled or failed")
        }
    }

    fun validatePrompt(title: String, text: String) {
        state.value = CameraViewModelState.PromptValidated("Yeaaahh !!")
        addNewPlayer(title, text)
    }

    fun rejectPrompt() {
        state.value = CameraViewModelState.PromptRejected("I hope you feel sorry to hurt our little API like that. Nobody loves rejection.")
    }

    private fun formatText(data: List<TextItem>) {
        val formattedString = data.joinToString(separator = " ") { it.text }
        state.value = CameraViewModelState.OCRSuccess(formattedString)
    }


    private fun addNewPlayer(title: String, text: String) {
        val newIndex = repository.getNewIndex()
        var newPlayer = Player(
            id = newIndex,
            title = title,
            image = 3,
            filePath = imagePath.value ?: "",
            content = text,
            updatedAt = Date(),
            createdAt = Date()
        )

        println("New Player Details: $newPlayer")

        try {
            val playerId = repository.savePlayer(newPlayer)
            if (playerId == null) {
                state.value = CameraViewModelState.Saved(newPlayer)
            } else {
                newPlayer.id = playerId
                val res = repository.updatePlayer(newPlayer)
                if (res != null){
                    state.value = CameraViewModelState.Saved(newPlayer)
                }else{
                    state.value = CameraViewModelState.OCRError("Failed to save player")
                }
            }
        } catch (e: Exception) {
            state.value = CameraViewModelState.OCRError("Error: ${e.message}")
        }
    }
}