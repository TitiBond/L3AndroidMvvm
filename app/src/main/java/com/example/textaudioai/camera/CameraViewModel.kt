package com.example.textaudioai.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.textaudioai.repositories.PaperPlayersRepository
import com.example.textaudioai.repositories.Player
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

sealed class CameraViewModelState {

    data class Loading(val message: String): CameraViewModelState()
    data class OCRError(val message: String): CameraViewModelState()
    data class OCRSuccess(val message: String): CameraViewModelState()
    data class PromptRejected(val message: String): CameraViewModelState()
    data class PromptValidated(val text: String): CameraViewModelState()
    data class Saved(val a: Any): CameraViewModelState()
}
class CameraViewModel  : ViewModel() {

    lateinit var repository: PaperPlayersRepository
    val imagePath = MutableLiveData<String>()
    lateinit var ninjaApi: NinjasApi
    lateinit var elevenlabsApi: ElevenlabsApi
    private lateinit var imageFile: File


    val state = MutableLiveData<CameraViewModelState>()

    fun createImageFile(context: Context): Uri {
        imageFile = File(context.filesDir, "picture.jpg")
        return FileProvider.getUriForFile(context, "com.example.android.fileprovider", imageFile)
    }

    private fun setImagePath(path: String) {
        imagePath.value = path
    }
    fun analysePicture(imageFile: File) {
        state.value = CameraViewModelState.Loading("Processing...")

        // CALL API
        val part = MultipartBody.Part
            .createFormData(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
        val call = ninjaApi.uploadImage(part, "EtVLP/4mR4f5MZHhQba0rA==YJCYfIwmCvWErpE1")
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
        val newPlayer = Player(
            id = newIndex,
            title = title,
            image = imagePath.value ?: "",
            filePath = "",
            duration = 0.0,
            content = text,
            updatedAt = Date(),
            createdAt = Date()
        )

        // CALL API
        val body = TTSBody(
            text,
            "eleven_monolingual_v1",
            TTSBodyVoiceSettings(
                0.5,
                0.5
            )
        )

        val call = elevenlabsApi.convertTTS(
            "audio/mpeg",
            "application/json",
            "ce26a99e3a2d05cd2f04375506aaec68",
            body
        )

        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println(response.body());
                val responseBody = response.body();
                val filePath = responseBody?.let { saveResponseBodyAsFile(it, newPlayer.title) };
                if (filePath == null) {
                    state.value = CameraViewModelState.OCRError("Error: Could not convert the text to audio, failed to save");
                    return;
                }

                try {
                    newPlayer.filePath = filePath;
                    val success = repository.savePlayer(newPlayer)
                    if (success) {
                        state.value = CameraViewModelState.Saved(newPlayer)
                    } else {
                        state.value = CameraViewModelState.OCRError("Failed to save player")
                    }
                } catch (e: Exception) {
                    state.value = CameraViewModelState.OCRError("Error: ${e.message}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                state.value = CameraViewModelState.OCRError("Error: Could not convert the text to audio")
            }
        })
    }

    private fun saveResponseBodyAsFile(responseBody: ResponseBody, title: String): String? {
        try {
            val fileDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "textToSpeech")

            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
            }

            val fileName = "$title.mp3"
            val outputFile = File(fileDirectory, fileName)

            val outputStream = FileOutputStream(outputFile)
            outputStream.write(responseBody.bytes())
            outputStream.close()

            return outputFile.absolutePath;
        } catch (e: IOException) {
            e.printStackTrace()
            return null;
        }
    }
}