package com.example.textaudioai.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

sealed class CameraViewModelState {

    data class Loading(val message: String): CameraViewModelState()
    data class OCRError(val message: String): CameraViewModelState()
    data class OCRSuccess(val message: String): CameraViewModelState()
    data class PromptValidated(val text: String): CameraViewModelState()
    data class PromptRejected(val text: String): CameraViewModelState()
    data class Saved(val a: Any): CameraViewModelState()
}
class CameraViewModel: ViewModel() {
    fun analysePicture(imageFile: File) {
        state.value = CameraViewModelState.Loading("Processing...")
        // CALL API

    }

    val state = MutableLiveData<CameraViewModelState>()


}