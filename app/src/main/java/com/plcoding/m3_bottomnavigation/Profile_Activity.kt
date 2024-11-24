package com.plcoding.m3_bottomnavigation

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileName: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var editButton: FloatingActionButton
    private var isEditing = false
    private lateinit var profileImage: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var imageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleImageSelection(it) }
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        Log.d(TAG, "TakePicture result: $success")
        if (success) {
            imageUri?.let { uri ->
                handleImageSelection(uri)
            } ?: run {
                Log.e(TAG, "ImageUri is null after successful capture")
                Toast.makeText(this, "Error: Image URI is null", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e(TAG, "Failed to capture image")
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Camera permission granted")
            takePhotoFromCamera()
        } else {
            Log.e(TAG, "Camera permission denied")
            Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profile)

        initializeViews()
        loadUserData()
        setupProfileImage()
        setupListeners()
    }
    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with image selection
                loadProfileImage()
            } else {
                Toast.makeText(this, "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun initializeViews() {
        profileName = findViewById(R.id.profileName)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        editButton = findViewById(R.id.editButton)
        profileImage = findViewById(R.id.profileImage)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
    }

    private fun loadUserData() {
        val phoneEmail = sharedPreferences.getString("PHONE_EMAIL", "") ?: ""
        val password = sharedPreferences.getString("PASSWORD", "") ?: ""
        val name = sharedPreferences.getString("NAME", "John Doe") ?: "John Doe"

        profileName.setText(name)
        phoneNumberEditText.setText(phoneEmail)
        passwordEditText.setText(password)
    }

    private fun setupListeners() {
        editButton.setOnClickListener {
            if (isEditing) {
                saveUserData()
                disableEditing()
            } else {
                enableEditing()
            }
            isEditing = !isEditing
        }

        profileImage.setOnClickListener {
            if (isEditing) {
                showImageSelectionDialog()
            }
        }
    }


    private fun showImageSelectionDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndTakePhoto()
                    1 -> choosePhotoFromGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Camera permission already granted")
                takePhotoFromCamera()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) -> {
                Log.d(TAG, "Showing camera permission rationale")
                showCameraPermissionRationale()
            }
            else -> {
                Log.d(TAG, "Requesting camera permission")
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showCameraPermissionRationale() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Camera Permission Required")
            .setMessage("The app needs access to your camera to take a profile picture.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun takePhotoFromCamera() {
        try {
            val photoFile = createImageFile()
            Log.d(TAG, "Photo file created: ${photoFile.absolutePath}")
            imageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
            Log.d(TAG, "FileProvider URI: $imageUri")
            takePhoto.launch(imageUri)
        } catch (e: Exception) {
            Log.e(TAG, "Error taking photo", e)
            Toast.makeText(this, "Error taking photo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun choosePhotoFromGallery() {
        pickImage.launch("image/*")
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            Log.d(TAG, "Handling image selection for URI: $uri")
            if (ImageUtils.saveImageToInternalStorage(this, uri)) {
                loadProfileImage() // Reload the image after saving
                Log.d(TAG, "Image saved and set successfully")
            } else {
                Log.e(TAG, "Failed to save image")
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling image selection", e)
            Toast.makeText(this, "Error setting image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveProfileImageUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, "profile_image.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val savedUri = Uri.fromFile(file)
            sharedPreferences.edit().putString("PROFILE_IMAGE_URI", savedUri.toString()).apply()
            Log.d(TAG, "Saved image URI: $savedUri")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving profile image", e)
            Toast.makeText(this, "Error saving profile image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage() {
        val bitmap = ImageUtils.loadImageFromInternalStorage(this)
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap)
            Log.d(TAG, "Profile image loaded successfully")
        } else {
            Log.d(TAG, "No saved profile image found, setting default image")
            profileImage.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun setupProfileImage() {
        loadProfileImage()
        profileImage.setOnClickListener {
            if (isEditing) {
                showImageSelectionDialog()
            }
        }
    }

    private fun saveUserData() {
        val updatedName = profileName.text.toString()
        val updatedPhoneEmail = phoneNumberEditText.text.toString()
        val updatedPassword = passwordEditText.text.toString()

        with(sharedPreferences.edit()) {
            putString("NAME", updatedName)
            putString("PHONE_EMAIL", updatedPhoneEmail)
            putString("PASSWORD", updatedPassword)
            apply()
        }

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
    }

    private fun enableEditing() {
        profileName.isEnabled = true
        phoneNumberEditText.isEnabled = true
        passwordEditText.isEnabled = true
        editButton.setImageResource(R.drawable.ic_save)
        profileImage.isClickable = true
    }

    private fun disableEditing() {
        profileName.isEnabled = false
        phoneNumberEditText.isEnabled = false
        passwordEditText.isEnabled = false
        editButton.setImageResource(R.drawable.ic_edit)
        profileImage.isClickable = false
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val TAG = "ProfileActivity"
    }
}