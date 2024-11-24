package com.plcoding.m3_bottomnavigation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MedicalActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etDob: EditText
    private lateinit var etBloodType: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etAllergies: EditText
    private lateinit var etPregnancyStatus: EditText
    private lateinit var etMedications: EditText
    private lateinit var etAddress: EditText
    private lateinit var etMedicalNotes: EditText
    private lateinit var etOrganDonor: EditText
    private lateinit var editButton: FloatingActionButton

    private var isEditMode = false

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_medical)

        // Initialize views
        initializeViews()

        val sharedPref = getSharedPreferences("MedicalInfo", Context.MODE_PRIVATE)

        // Disable editing fields by default
        disableFields()

        // Set up date picker
        setupDatePicker()

        // Load saved data
        loadData(sharedPref)

        // Set up edit button
        setupEditButton(sharedPref)

        // Set up the top app bar using Compose
        setupTopAppBar()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun setupTopAppBar() {
        // Find the Compose view in your layout
        val composeView = findViewById<ComposeView>(R.id.composeView)

        composeView.setContent {
            MaterialTheme {
                TopAppBar(
                    title = {
                        Text(
                            text = "Medical Info",
                            color = Color.White,
                            modifier = Modifier.padding(top=21.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            finish() // This will close the current activity
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                modifier = Modifier.padding(top=20.dp),
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1f1f1f)
                    ),
                    modifier = Modifier.height(59.dp)
                )
            }
        }
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etName)
        etDob = findViewById(R.id.etDob)
        etBloodType = findViewById(R.id.etBloodType)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        etAllergies = findViewById(R.id.etAllergies)
        etPregnancyStatus = findViewById(R.id.etPregnancyStatus)
        etMedications = findViewById(R.id.etMedications)
        etAddress = findViewById(R.id.etAddress)
        etMedicalNotes = findViewById(R.id.etMedicalNotes)
        etOrganDonor = findViewById(R.id.etOrganDonor)
        editButton = findViewById(R.id.editButton)
    }

    private fun setupEditButton(sharedPref: android.content.SharedPreferences) {
        editButton.setOnClickListener {
            if (isEditMode) {
                // Save mode
                saveData(sharedPref)
                disableFields()
                editButton.setImageResource(R.drawable.ic_edit) // Make sure you have this drawable
                isEditMode = false
            } else {
                // Edit mode
                enableFields()
                editButton.setImageResource(R.drawable.ic_save) // Make sure you have this drawable
                isEditMode = true
            }
        }
    }

        private fun setupDatePicker() {
            etDob.setOnClickListener {
                if (isEditMode) {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    DatePickerDialog(
                        this,
                        { _, selectedYear, selectedMonth, selectedDay ->
                            etDob.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                        },
                        year, month, day
                    ).show()
                }
            }
        }


    private fun saveData(sharedPref: android.content.SharedPreferences) {
        val editor = sharedPref.edit()
        editor.putString("name", etName.text.toString())
        editor.putString("dob", etDob.text.toString())
        editor.putString("bloodType", etBloodType.text.toString())
        editor.putString("height", etHeight.text.toString())
        editor.putString("weight", etWeight.text.toString())
        editor.putString("allergies", etAllergies.text.toString())
        editor.putString("pregnancyStatus", etPregnancyStatus.text.toString())
        editor.putString("medications", etMedications.text.toString())
        editor.putString("address", etAddress.text.toString())
        editor.putString("medicalNotes", etMedicalNotes.text.toString())
        editor.putString("organDonor", etOrganDonor.text.toString())
        editor.apply()
    }

    private fun loadData(sharedPref: android.content.SharedPreferences) {
        etName.setText(sharedPref.getString("name", ""))
        etDob.setText(sharedPref.getString("dob", ""))
        etBloodType.setText(sharedPref.getString("bloodType", ""))
        etHeight.setText(sharedPref.getString("height", ""))
        etWeight.setText(sharedPref.getString("weight", ""))
        etAllergies.setText(sharedPref.getString("allergies", ""))
        etPregnancyStatus.setText(sharedPref.getString("pregnancyStatus", ""))
        etMedications.setText(sharedPref.getString("medications", ""))
        etAddress.setText(sharedPref.getString("address", ""))
        etMedicalNotes.setText(sharedPref.getString("medicalNotes", ""))
        etOrganDonor.setText(sharedPref.getString("organDonor", ""))
    }

    private fun disableFields() {
        etName.isEnabled = false
        etDob.isEnabled = false
        etBloodType.isEnabled = false
        etHeight.isEnabled = false
        etWeight.isEnabled = false
        etAllergies.isEnabled = false
        etPregnancyStatus.isEnabled = false
        etMedications.isEnabled = false
        etAddress.isEnabled = false
        etMedicalNotes.isEnabled = false
        etOrganDonor.isEnabled = false
    }

    private fun enableFields() {
        etName.isEnabled = true
        etDob.isEnabled = true
        etBloodType.isEnabled = true
        etHeight.isEnabled = true
        etWeight.isEnabled = true
        etAllergies.isEnabled = true
        etPregnancyStatus.isEnabled = true
        etMedications.isEnabled = true
        etAddress.isEnabled = true
        etMedicalNotes.isEnabled = true
        etOrganDonor.isEnabled = true
    }
}