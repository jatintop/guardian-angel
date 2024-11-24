package com.plcoding.m3_bottomnavigation

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var phoneEmailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_signup)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)



        // Initialize views
        phoneEmailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        signUpButton = findViewById(R.id.create_account_button)

        // Handle sign-up button click
        signUpButton.setOnClickListener {
            val phoneEmail = phoneEmailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (phoneEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save the user data and set login state to true
            with(sharedPreferences.edit()) {
                putString("PHONE_EMAIL", phoneEmail)
                putString("PASSWORD", password)
                putBoolean("IS_LOGGED_IN", true)
                apply()
            }

            navigateToMedicalActivity()
        }
    }

    private fun navigateToMedicalActivity() {
        Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close SignUpActivity so user can't navigate back to it
    }
}