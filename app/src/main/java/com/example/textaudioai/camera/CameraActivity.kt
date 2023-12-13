package com.example.textaudioai.camera

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityCameraBinding
import com.example.textaudioai.databinding.ActivityVoiceListBinding
import com.example.textaudioai.voices.VoiceListViewModelState
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    val viewModel: CameraViewModel by viewModels()
    private lateinit var imageFile: File

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                val value = it.data?.getStringExtra("input")
                viewModel.analysePicture(imageFile)

            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.openCameraButton.setOnClickListener {
            takePicture()
        }


    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageFile = File(filesDir, "test.jpg")

        val uri = FileProvider.getUriForFile(
            this,
            "com.example.android.fileprovider",
            imageFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getResult.launch(takePictureIntent)
    }
}