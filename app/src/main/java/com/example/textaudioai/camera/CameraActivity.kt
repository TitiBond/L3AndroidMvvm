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
import com.example.textaudioai.databinding.ActivityCameraBinding
import okhttp3.MultipartBody
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private val viewModel: CameraViewModel by viewModels()
    private lateinit var imageFile: File

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                viewModel.analysePicture(imageFile)
                viewModel.setImagePath(imageFile.absolutePath)

            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(NinjasApi::class.java)
        viewModel.api = api

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.openCameraButton.setOnClickListener {
            takePicture()
        }

        // observes livedata to retrieve image when there is one
        viewModel.imagePath.observe(this) { path ->
            displayImage(path)
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // stores image to display it in activity
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