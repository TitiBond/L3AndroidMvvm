package com.example.textaudioai.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
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
import java.io.FileOutputStream
import java.io.InputStream

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private val viewModel: CameraViewModel by viewModels()
    private lateinit var imageFile: File

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val isCamera = it.data == null || it.data!!.data == null
                // checks if the source is camera or gallery
                if (isCamera) {
                    viewModel.analysePicture(imageFile)
                    viewModel.setImagePath(imageFile.absolutePath)
                } else {
                    it.data?.data?.let { uri ->
                        val galleryImageFile = uriToFile(uri, this)
                        viewModel.analysePicture(galleryImageFile)
                        viewModel.setImagePath(galleryImageFile.absolutePath)
                    }
                }
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
        binding.openGalleryButton.setOnClickListener {
            openGallery()
        }

        viewModel.state.observe(this) {
            updateUI(it!!)
        }

        // observes livedata to retrieve image when there is one
        viewModel.imagePath.observe(this) { path ->
            displayImage(path)
        }

        binding.validatePromptButton.visibility = View.GONE
        binding.rejectPromptButton.visibility = View.GONE

    }

    private fun updateUI(state: CameraViewModelState) {
        when (state) {
            is CameraViewModelState.Loading -> {}

            is CameraViewModelState.OCRError -> {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }

            is CameraViewModelState.OCRSuccess -> {
                displayText(state.text)
                showValidationButtons()
            }
            is CameraViewModelState.PromptRejected -> {
                resetUI()
            }
            is CameraViewModelState.PromptValidated -> {
                finish()
            }
            is CameraViewModelState.Saved -> {
                finish()
            }
        }
    }

    private fun showValidationButtons() {
        binding.openCameraButton.visibility = View.GONE
        binding.openGalleryButton.visibility = View.GONE

        binding.validatePromptButton.visibility = View.VISIBLE
        binding.rejectPromptButton.visibility = View.VISIBLE

        binding.validatePromptButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val text = binding.apiResponseTextView.text.toString()
            viewModel.validatePrompt(title, text)
        }
        binding.rejectPromptButton.setOnClickListener {
            viewModel.rejectPrompt()
        }
    }

    private fun showCameraButtons() {
        binding.openCameraButton.visibility = View.VISIBLE
        binding.openGalleryButton.visibility = View.VISIBLE
        binding.validatePromptButton.visibility = View.GONE
        binding.rejectPromptButton.visibility = View.GONE
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getResult.launch(intent)
    }

    private fun displayImage(imagePath: String) {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            binding.photoImageView.setImageBitmap(bitmap)
        }
    }

    private fun displayText(text: String) {
        binding.apiResponseTextView.text = text
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
        val inputStream = contentResolver.openInputStream(uri) as InputStream
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        return tempFile
    }

    private fun resetUI() {
        binding.photoImageView.setImageBitmap(null)

        binding.apiResponseTextView.text = ""
        binding.titleEditText.text.clear()

        showCameraButtons()
    }
}