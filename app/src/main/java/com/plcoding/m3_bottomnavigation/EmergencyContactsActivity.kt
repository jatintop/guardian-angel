package com.plcoding.m3_bottomnavigation

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Add this import
import android.Manifest
import android.app.Activity

// Add this import
import android.app.Service
import android.os.IBinder

class EmergencyContactsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestContactPermission()
        setContent {
            val context = LocalContext.current
            val viewModel: EmergencyContactsViewModel = viewModel(
                factory = EmergencyContactsViewModel.provideFactory(
                    repository = EmergencyContactRepository(
                        contactDao = AppDatabase.getDatabase(context).emergencyContactDao()
                    )
                )
            )
            EmergencyContactsScreen(viewModel)
        }
    }
    private fun requestContactPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                102
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            102 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, you can now access the contacts
                    startService(Intent(this, ContactService::class.java))
                } else {
                    // Permission denied, handle this case (maybe show a message to the user)
                }
                return
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactsScreen(viewModel: EmergencyContactsViewModel) {
    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(contacts) {
        println("Contacts updated in UI: $contacts")
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = { uri ->
            uri?.let {
                val contactId = uri.lastPathSegment
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                context.contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    ContactsContract.Contacts._ID + " = ?",
                    arrayOf(contactId),
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex =
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val name = cursor.getString(nameIndex)
                        val phoneNumberCursor = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )
                        phoneNumberCursor?.use {
                            if (it.moveToFirst()) {
                                val numberIndex =
                                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                val number = it.getString(numberIndex)
                                viewModel.addContact(
                                    EmergencyContact(
                                        id = UUID.randomUUID().toString(),
                                        name = name,
                                        phoneNumber = number
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency contacts", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.finish()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            modifier = Modifier.padding(top=8.dp),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1f1f1f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier.padding(top = 0.dp) // Add top padding
            )
        },
        containerColor = Color(0xFF1f1f1f)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(contacts) { contact ->
                ContactItem(contact, onDelete = { viewModel.deleteContact(it) })
            }
            item {
                AddContactButton(onClick = { launcher.launch(null) })
            }
        }
    }
}


@Composable
fun ContactItem(contact: EmergencyContact, onDelete: (EmergencyContact) -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Contact avatar",
            tint = Color(0xFF1BBAED),
            modifier = Modifier.size(48.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(text = contact.name, style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFFFFF))
            Text(text = "Mobile: ${contact.phoneNumber}", style = MaterialTheme.typography.bodyMedium, color = Color(
                0xFF8D8C8C
            )
            )
        }
        IconButton(onClick = { onDelete(contact) }) {
            Icon(Icons.Default.Close, contentDescription = "Remove contact", tint = Color(0xFFFF6E6E),)
        }
    }
}

@Composable
fun AddContactButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add contact", tint = Color(0xFFFFC107)) // Change the icon color
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add contact", color = Color(0xFFFFC107)) // Change the text color
    }
}

class ContactService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle the contact addition in the background
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}