package com.example.textaudioai.camera

import android.content.ContentResolver
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
import com.example.textaudioai.R
import com.example.textaudioai.databinding.ActivityCameraBinding
import com.example.textaudioai.repositories.PlayersRepository
import java.io.File
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileOutputStream
import java.io.InputStream

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        viewModel.repository = PlayersRepository();

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

        binding.testUploadImageButton.setOnClickListener {
            launchTest()
        }

        viewModel.state.observe(this) {
            updateUI(it!!)
        }

        // observes livedata to retrieve image when there is one
        viewModel.imagePath.observe(this) { path ->
            displayImage(path)
        }

    resetUI()
    }

    private fun launchTest() {
        val drawableId = R.drawable.image
        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(drawableId))
            .appendPath(resources.getResourceTypeName(drawableId))
            .appendPath(resources.getResourceEntryName(drawableId))
            .build()

        val imageFile = uriToFile(imageUri, this)

        viewModel.analysePicture(imageFile)
    }

    private fun resetUI() {
        binding.openCameraButton.visibility = View.VISIBLE
        binding.openGalleryButton.visibility = View.VISIBLE
        binding.testUploadImageButton.visibility = View.VISIBLE
        binding.validatePromptButton.visibility = View.GONE
        binding.rejectPromptButton.visibility = View.GONE
        binding.apiResponseTextView.visibility = View.GONE
        binding.photoImageView.setImageBitmap(null)
        binding.apiResponseTextView.text = ""
        binding.titleEditText.text.clear()
    }

    private fun updateUI(state: CameraViewModelState) {
        when (state) {
            is CameraViewModelState.Loading -> {}

            is CameraViewModelState.OCRError -> {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }

            is CameraViewModelState.OCRSuccess -> {
                displayText(state.message)
                showValidationButtons()
            }
            is CameraViewModelState.PromptRejected -> {
                resetUI()
            }
            is CameraViewModelState.PromptValidated -> {
                Toast.makeText(this, "Player created successfully!", Toast.LENGTH_LONG).show()
            }
            is CameraViewModelState.Saved -> {
                finish()
            }

            else -> {}
        }
    }

    private fun showValidationButtons() {
        binding.apiResponseTextView.visibility = View.VISIBLE
        binding.openCameraButton.visibility = View.GONE
        binding.openGalleryButton.visibility = View.GONE

        binding.validatePromptButton.visibility = View.VISIBLE
        binding.rejectPromptButton.visibility = View.VISIBLE

        binding.testUploadImageButton.visibility = View.GONE

        binding.validatePromptButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val text = binding.apiResponseTextView.text.toString()
            viewModel.validatePrompt(title, text)
        }
        binding.rejectPromptButton.setOnClickListener {
            viewModel.rejectPrompt()
        }
    }

    private fun takePicture() {
        val uri = viewModel.createImageFile(this)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
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

    // IMAGE UPLOAD FROM BOTH CAMERA AND GALLERY
    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.handleActivityResult(result.resultCode, result.data) { uri ->
                uriToFile(uri, this)
            }
        }

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
        val inputStream = contentResolver.openInputStream(uri) as InputStream
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        return tempFile
    }
}