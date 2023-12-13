package com.example.textaudioai.camera

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
                viewModel.analysePicture(imageFile)
                viewModel.setImagePath(imageFile.absolutePath)

            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.openCameraButton.setOnClickListener {
            takePicture()
        }

        viewModel.imagePath.observe(this) { path ->
            displayImage(path)
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageFile = File(filesDir, "picture.jpg")

        val uri = FileProvider.getUriForFile(
            this,
            "com.example.android.fileprovider",
            imageFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        getResult.launch(intent)
    }

    private fun displayImage(imagePath: String) {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            binding.photoImageView.setImageBitmap(bitmap)
        }
    }
}