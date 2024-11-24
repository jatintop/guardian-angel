package com.plcoding.m3_bottomnavigation

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.checkbox.MaterialCheckBox

class AngelForm : AppCompatActivity() {
    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var aadharInput: TextInputEditText
    private lateinit var bloodTypeInput: TextInputEditText
    private lateinit var agreeCheckbox: MaterialCheckBox
    private var formSubmitted = false

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_angel)

        // Initialize ComposeView for TopAppBar
        findViewById<androidx.compose.ui.platform.ComposeView>(R.id.composeView).setContent {
            TopBar(
                onBackPressed = { onBackPressed() }
            )
        }

        // Initialize views
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        nameInput = findViewById(R.id.nameInput)
        phoneInput = findViewById(R.id.phoneInput)
        aadharInput = findViewById(R.id.aadharInput)
        bloodTypeInput = findViewById(R.id.bloodTypeInput)
        agreeCheckbox = findViewById(R.id.agreeCheckbox)
    }

    private fun setupListeners() {
        // Setup verify button click listener
        findViewById<android.widget.Button>(R.id.verifyButton).setOnClickListener {
            formSubmitted = true
            if (validateInputs()) {
                handleSuccessfulRegistration()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate name
        if (nameInput.text.isNullOrBlank()) {
            nameInput.error = "Name is required"
            isValid = false
        }

        // Validate phone number
        if (phoneInput.text.isNullOrBlank()) {
            phoneInput.error = "Phone number is required"
            isValid = false
        } else if (!isValidPhoneNumber(phoneInput.text.toString())) {
            phoneInput.error = "Enter a valid 10-digit phone number"
            isValid = false
        }

        // Validate Aadhar number
        if (aadharInput.text.isNullOrBlank()) {
            aadharInput.error = "Aadhar number is required"
            isValid = false
        } else if (!isValidAadharNumber(aadharInput.text.toString())) {
            aadharInput.error = "Enter a valid 12-digit Aadhar number"
            isValid = false
        }

        // Validate blood type
        if (bloodTypeInput.text.isNullOrBlank()) {
            bloodTypeInput.error = "Blood type is required"
            isValid = false
        } else if (!isValidBloodType(bloodTypeInput.text.toString())) {
            bloodTypeInput.error = "Enter a valid blood type (e.g., A+, B-, O+, AB+)"
            isValid = false
        }

        // Validate checkbox
        if (!agreeCheckbox.isChecked) {
            Toast.makeText(this, "Please agree to share information", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun handleSuccessfulRegistration() {
        // Save enrollment status
        getSharedPreferences("angel_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("is_enrolled", true)
            .putString("name", nameInput.text?.toString())
            .putString("phone", phoneInput.text?.toString())
            .putString("aadhar", aadharInput.text?.toString())
            .putString("blood_type", bloodTypeInput.text?.toString())
            .apply()

        // Show success messages
        Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show()
        Toast.makeText(
            this,
            "You are now registered as a Guardian Angel. Your information will only be shared in case of emergencies.",
            Toast.LENGTH_LONG
        ).show()

        // Navigate back to previous screen after successful registration
        finish()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }

    private fun isValidAadharNumber(aadhar: String): Boolean {
        return aadhar.length == 12 && aadhar.all { it.isDigit() }
    }

    private fun isValidBloodType(bloodType: String): Boolean {
        val validBloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        return validBloodTypes.contains(bloodType.uppercase())
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar(onBackPressed: () -> Unit) {
        TopAppBar(
            title = {
                Text(
                    "Guardian Angel Registration",
                    color = Color.White // Set the title color to white
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White // Set the icon color to white
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1f1f1f)
            )
        )
    }
}