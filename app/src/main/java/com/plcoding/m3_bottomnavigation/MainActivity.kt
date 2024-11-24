package com.plcoding.m3_bottomnavigation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.RingtoneManager
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.plcoding.m3_bottomnavigation.ui.theme.M3BottomNavigationTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.DropdownMenuItem
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.telephony.SmsManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.gms.location.LocationServices
import com.plcoding.m3_bottomnavigation.Constants.END_NOTIFICATION_ID


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)


private const val CHANNEL_ID = "activity_channel_id"
const val NOTIFICATION_ID = 1
const val ACTION_YES = "com.plcoding.m3_bottomnavigation.ACTION_YES"
const val ACTION_NO = "com.plcoding.m3_bottomnavigation.ACTION_NO"
const val REQUEST_NOTIFICATION_PERMISSION = 1001 // or any other unique integer

// @OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    object Constants {
        const val REQUEST_CALL_PHONE_PERMISSION = 1
        const val REQUEST_POST_NOTIFICATIONS_PERMISSION = 2
        const val REQUEST_NOTIFICATION_PERMISSION = 3
    }

    private val CHANNEL_ID = "activity_notifications"



    private fun requestNotificationPermission(activity: android.app.Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                Constants.REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_NOTIFICATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can send notifications
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Notification permission required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        requestNotificationPermission(this)

        setContent {
            M3BottomNavigationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
                    var showGeminiChatScreen by rememberSaveable { mutableStateOf(false) }

                    val items = listOf(
                        BottomNavigationItem(
                            title = "Home",
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home,
                            hasNews = false
                        ),
                        BottomNavigationItem(
                            title = "Features",
                            selectedIcon = Icons.Filled.Lock,
                            unselectedIcon = Icons.Outlined.Lock,
                            hasNews = false
                        ),
                        BottomNavigationItem(
                            title = "Gemini",
                            selectedIcon = Icons.Filled.Star,
                            unselectedIcon = Icons.Outlined.Star,
                            hasNews = false
                        ),
                        BottomNavigationItem(
                            title = "Your Info",
                            selectedIcon = Icons.Filled.AccountCircle,
                            unselectedIcon = Icons.Outlined.AccountCircle,
                            hasNews = false
                        )
                    )



                        Scaffold(
                            bottomBar = {
                                NavigationBar {
                                    items.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = {
                                                if (index == 1) {
                                                    // Features tab clicked
                                                    selectedItemIndex = index
                                                } else {
                                                    selectedItemIndex = index
                                                }
                                            },
                                            label = {
                                                Text(text = item.title)
                                            },
                                            alwaysShowLabel = true,
                                            icon = {
                                                BadgedBox(
                                                    badge = {
                                                        if (item.badgeCount != null) {
                                                            Badge {
                                                                Text(text = item.badgeCount.toString())
                                                            }
                                                        } else if (item.hasNews) {
                                                            Badge()
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (index == selectedItemIndex) {
                                                            item.selectedIcon
                                                        } else item.unselectedIcon,
                                                        contentDescription = item.title
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                when (selectedItemIndex) {
                                    0 -> PersonalSafetyScreen()
                                    1 -> FeaturesScreen() // No Talk to Gemini button here
                                    3 -> InfoScreen()
                                    2 -> GeminiChatContent()

                                }
                            }
                        }

                }
            }
        }
    }
}


//private fun createNotificationChannel(context: Context) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val name = "Timer Channel"
//        val descriptionText = "Channel for timer notifications"
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val channel = NotificationChannel("timer_channel", name, importance).apply {
//            description = descriptionText
//        }
//        val notificationManager: NotificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }
//}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalSafetyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1f1f1f))
            .padding(
                start = 16.dp,
                top = 32.dp,
                end = 16.dp,
            )
    ) {
        // Top Row with Icon, Title, and Profile Image
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Shield Icon",
                tint = Color(0xFFEB4949),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Guardian Angel",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold

            )

        }

        Spacer(modifier = Modifier.height(36.dp))

        SOSButton()

        Spacer(modifier = Modifier.height(13.dp))

        // Start Activity Card
        StartActivityCard()

        Spacer(modifier = Modifier.height(7.dp))

        // Bottom Buttons
        BottomButtons()
    }
}

@Composable
fun SOSButton() {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission is granted, make the call
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:112")
                }
                context.startActivity(intent)
            } else {
                // Permission is denied, handle accordingly
                Toast.makeText(context, "Permission required to make calls", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Centered SOS Button
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                when {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED -> {
                        // Permission already granted
                        val intent = Intent(Intent.ACTION_CALL).apply {
                            data = Uri.parse("tel:112")
                        }
                        context.startActivity(intent)

                        requestLocationPermission(context)

                        requestLocationPermissionForGuardianAngels(context)
                    }
                    else -> {
                        // Request permission
                        permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE3838)),
            shape = RoundedCornerShape(19.dp),
            modifier = Modifier
                .width(180.dp)
                .height(100.dp)
        ) {
            Text(
                text = "SOS",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Permission Request Constants
const val REQUEST_POST_NOTIFICATIONS_PERMISSION = 1002

private fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
            false
        } else {
            true
        }
    } else {
        true
    }
}
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Timer Channel"
        val descriptionText = "Channel for timer notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showTimerNotification(context: Context, durationInMillis: Long) {
    createNotificationChannel(context)

    if (!checkNotificationPermission(context)) return // Ensure permission is checked

    val notificationManager = NotificationManagerCompat.from(context)
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_timer) // Ensure this icon exists
        .setContentTitle("Activity Started")
        .setContentText("Timer is running")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}



// Add this new receiver class
class DismissNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}


// Modify your update notification method
fun updateNotification(context: Context, timeLeft: Long, withSound: Boolean) {
    // If time is zero or negative, clear the notification
    if (timeLeft <= 0) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        triggerEndNotification(context)
        return
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val totalSeconds = (timeLeft / 1000)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    // Format time as hh:mm:ss
    val timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    // Explicitly check for 00:00:00 and dismiss
    if (hours == 0L && minutes == 0L && seconds == 0L) {
        notificationManager.cancel(NOTIFICATION_ID)
        triggerEndNotification(context)
        return
    }

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_timer)
        .setContentTitle("Activity Started")
        .setContentText("Time left: $timeText")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOnlyAlertOnce(true)

    if (withSound) {
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
    } else {
        notificationBuilder.setSilent(true)
    }

    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}

// Add these constants at the top of your Constants object
object Constants {
    const val REQUEST_NOTIFICATION_PERMISSION = 1001
    const val NOTIFICATION_ID = 1
    const val END_NOTIFICATION_ID = 2
    const val CHANNEL_ID = "TimerChannel"
    const val ACTION_YES = "com.yourdomain.app.ACTION_YES"
    const val ACTION_NO = "com.yourdomain.app.ACTION_NO"
}

fun triggerEndNotification(context: Context) {
    createNotificationChannel(context)

    if (!checkNotificationPermission(context)) return

    val notificationManager = NotificationManagerCompat.from(context)

    // Create PendingIntent for "Yes" action
    val yesIntent = Intent(context, NotificationActionReceiver::class.java).apply {
        action = ACTION_YES
    }
    val yesPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        yesIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Create PendingIntent for "No" action
    val noIntent = Intent(context, NotificationActionReceiver::class.java).apply {
        action = ACTION_NO
    }
    val noPendingIntent = PendingIntent.getBroadcast(
        context,
        1,
        noIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Build notification with actions
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_timer)
        .setContentTitle("Activity Ended")
        .setContentText("Are you safe?")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_check, "Yes", yesPendingIntent)
        .addAction(R.drawable.ic_close, "No", noPendingIntent)
        .setOngoing(true) // Makes the notification persistent until user responds

    // Use vibration pattern to get attention
    notificationBuilder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

    try {
        notificationManager.notify(END_NOTIFICATION_ID, notificationBuilder.build())
    } catch (e: SecurityException) {
        Log.e("Notification", "Permission denied: ${e.message}")
    }
}

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            // Dismiss the notification
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(END_NOTIFICATION_ID)

            when (intent.action) {
                ACTION_YES -> {
                    // Show toast message
                    Toast.makeText(context, "Glad you're safe!", Toast.LENGTH_SHORT).show()
                }
                ACTION_NO -> {
                    // Show a toast to indicate SOS is being triggered
                    Toast.makeText(context, "Triggering SOS...", Toast.LENGTH_SHORT).show()
                    // Trigger SOS functionality
                    triggerSOSFunctionality(context)
                }
            }
        } catch (e: Exception) {
            Log.e("NotificationActionReceiver", "Error in onReceive", e)
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun triggerSOSFunctionality(context: Context) {
        try {
            // Check location permissions
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        shareLocationViaSMS(context, it, isGuardianAngel = true)
                    }
                }
            }

            // Existing call functionality
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:112")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("NotificationActionReceiver", "Error in triggerSOSFunctionality", e)
            Toast.makeText(context, "Failed to trigger SOS", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartActivityCard() {
    Spacer(modifier = Modifier.height(10.dp))
    var expandedActivity by remember { mutableStateOf(false) }
    var expandedDuration by remember { mutableStateOf(false) }
    var selectedActivity by remember { mutableStateOf("Walking Alone") }
    var selectedDuration by remember { mutableStateOf("1 hour") }
    var timerRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(0L) }
    var showCustomDurationDialog by remember { mutableStateOf(false) }
    var showCustomActivityDialog by remember { mutableStateOf(false) }
    var customHours by remember { mutableStateOf("") }
    var customMinutes by remember { mutableStateOf("") }
    var customActivity by remember { mutableStateOf("") }
    val context = LocalContext.current

    val activities = listOf(
        "Walking Alone",
        "Going for a run",
        "Taking Transportation",
        "Hiking",
        "Add Custom Activity"
    )

    val durations = listOf(
        "3 secs", "15 mins", "30 mins", "1 hour", "Add Custom Duration"
    )

    // Custom Activity Dialog
    if (showCustomActivityDialog) {
        AlertDialog(
            onDismissRequest = { showCustomActivityDialog = false },
            title = {
                Text(
                    "Enter Custom Activity",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = customActivity,
                    onValueChange = { customActivity = it },
                    label = { Text("Activity Name", color = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF7C544),
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customActivity.isNotBlank()) {
                            selectedActivity = customActivity
                            showCustomActivityDialog = false
                            customActivity = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7C544))
                ) {
                    Text("Confirm", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCustomActivityDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF3F3F3F),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // Custom Duration Dialog
    if (showCustomDurationDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDurationDialog = false },
            title = {
                Text(
                    "Enter Custom Duration",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    // Hours Input
                    OutlinedTextField(
                        value = customHours,
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                customHours = it
                            }
                        },
                        label = { Text("Hours", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF7C544),
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Minutes Input
                    OutlinedTextField(
                        value = customMinutes,
                        onValueChange = {
                            if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() < 60)) {
                                customMinutes = it
                            }
                        },
                        label = { Text("Minutes", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF7C544),
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hours = customHours.toIntOrNull() ?: 0
                        val minutes = customMinutes.toIntOrNull() ?: 0

                        if (hours > 0 || minutes > 0) {
                            val durationText = buildString {
                                if (hours > 0) {
                                    append("$hours hour")
                                    if (hours > 1) append("s")
                                }
                                if (hours > 0 && minutes > 0) {
                                    append(" ")
                                }
                                if (minutes > 0) {
                                    append("$minutes min")
                                    if (minutes > 1) append("s")
                                }
                            }
                            selectedDuration = durationText
                            showCustomDurationDialog = false
                            customHours = ""
                            customMinutes = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7C544))
                ) {
                    Text("Confirm", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCustomDurationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF3F3F3F),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
    // Start Timer Function
    fun startTimer(selectedDuration: String) {
        val durationInMillis = when {
            selectedDuration.contains("hour") && selectedDuration.contains("min") -> {
                val hours = selectedDuration.split(" ")[0].toLong()
                val minutes = selectedDuration.split(" ")[2].toLong()
                (hours * 60 * 60 * 1000) + (minutes * 60 * 1000)
            }
            selectedDuration.contains("hour") -> {
                val hours = selectedDuration.split(" ")[0].toLong()
                hours * 60 * 60 * 1000
            }
            selectedDuration.contains("min") -> {
                val minutes = selectedDuration.split(" ")[0].toLong()
                minutes * 60 * 1000
            }
            selectedDuration == "3 secs" -> 3 * 1000L
            selectedDuration == "15 mins" -> 15 * 60 * 1000L
            selectedDuration == "30 mins" -> 30 * 60 * 1000L
            selectedDuration == "1 hour" -> 60 * 60 * 1000L
            selectedDuration == "2 hours" -> 2 * 60 * 60 * 1000L
            selectedDuration == "3 hours" -> 3 * 60 * 60 * 1000L
            selectedDuration == "8 hours" -> 8 * 60 * 60 * 1000L
            else -> 60 * 60 * 1000L // Default to 1 hour if no match
        }

        // Check and request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as ComponentActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
                return // Return early if permission is not granted
            }
        }

        // Start the timer and show notification
        timerRunning = true
        timeLeft = durationInMillis
        showTimerNotification(context, durationInMillis)

        object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                // Update notification silently (no sound or vibration)
                updateNotification(context, timeLeft, false)
            }

            override fun onFinish() {
                timerRunning = false
                timeLeft = 0
                // Trigger the end notification with sound
                triggerEndNotification(context)
            }

        }.start()
    }

    Card(
        modifier = Modifier
            .padding(16.dp, end = 18.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3F3F3F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_activity),
                    contentDescription = "Activity Icon",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Activity",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Activity Dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedActivity = true }
                    .background(Color(0xFF424242), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFF2F2F2), RoundedCornerShape(13.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_walking),
                        contentDescription = "Activity Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedActivity,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = expandedActivity,
                    onDismissRequest = { expandedActivity = false },
                    modifier = Modifier
                        .background(Color(0xFF424242), shape = RoundedCornerShape(8.dp))
                ) {
                    activities.forEach { activity ->
                        DropdownMenuItem(
                            text = { Text(activity, color = Color.White) },
                            onClick = {
                                if (activity == "Add Custom Activity") {
                                    showCustomActivityDialog = true
                                } else {
                                    selectedActivity = activity
                                }
                                expandedActivity = false
                            },
                            modifier = Modifier
                                .background(Color(0xFF424242), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Duration Dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedDuration = true }
                    .background(Color(0xFF424242), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFF2F2F2), RoundedCornerShape(13.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_timer),
                        contentDescription = "Duration Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedDuration,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = expandedDuration,
                    onDismissRequest = { expandedDuration = false },
                    modifier = Modifier
                        .background(Color(0xFF424242), shape = RoundedCornerShape(8.dp))
                ) {
                    durations.forEach { duration ->
                        DropdownMenuItem(
                            text = { Text(duration, color = Color.White) },
                            onClick = {
                                if (duration == "Add Custom Duration") {
                                    showCustomDurationDialog = true
                                } else {
                                    selectedDuration = duration
                                }
                                expandedDuration = false
                            },
                            modifier = Modifier
                                .background(Color(0xFF424242), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (!timerRunning) {
                            startTimer(selectedDuration)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7C544)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = if (timerRunning) "..." else "Start",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BottomButtons() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // "Share Live Location" Button
            OutlinedButton(
                onClick = {
                    requestLocationPermission(context)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6E6E)),
                border = BorderStroke(2.dp, Color(0xFFFF6E6E)),
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .padding(end = 8.dp)
                    .background(
                        Color(0xFF363636),
                        shape = RoundedCornerShape(40.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Share Live Location",
                            tint = Color(0xFFFF6E6E),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Share Live\nLocation",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Call112Button
            Call112Button()
        }
    }
}

// Request location permission
fun requestLocationPermission(context: Context) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS),
            100
        )
    } else {
        // Get the current location and share it via SMS
        getCurrentLocation(context)
    }
}

fun getCurrentLocation(context: Context) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // List of potential providers in order of preference
    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )

    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            shareLocationViaSMS(context, location)
            locationManager.removeUpdates(this)
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(context, "$provider is disabled", Toast.LENGTH_SHORT).show()
        }
    }

    try {
        // Try each provider until we get a location
        for (provider in providers) {
            if (locationManager.isProviderEnabled(provider)) {
                try {
                    locationManager.requestLocationUpdates(
                        provider,
                        0L,
                        0f,
                        locationListener
                    )

                    val lastKnownLocation = locationManager.getLastKnownLocation(provider)
                    if (lastKnownLocation != null) {
                        shareLocationViaSMS(context, lastKnownLocation)
                        return
                    }
                } catch (e: SecurityException) {
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                    return
                } catch (e: Exception) {
                    // Log or handle specific provider failure
                    Log.e("LocationServices", "Error with $provider: ${e.message}")
                }
            }
        }

        // If no provider works
        Toast.makeText(context, "Unable to retrieve location on this device", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Location services error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// Share the location via SMS
fun shareLocationViaSMS(context: Context, location: Location, isGuardianAngel: Boolean = false) {
    val smsManager = SmsManager.getDefault()
    val message =
        "My current location is: https://www.google.com/maps/place/${location.latitude},${location.longitude}\n\nThis message was sent via Guardian Angels"


    // Get all emergency contacts from the database
    val database = AppDatabase.getDatabase(context)
    val contactDao = database.emergencyContactDao()

    // Launch a coroutine to handle database operation
    GlobalScope.launch(Dispatchers.IO) {
        val emergencyContacts = contactDao.getAllContacts()

        // Switch to main thread for SMS sending and UI updates
        withContext(Dispatchers.Main) {
            var contactsFound = false

            emergencyContacts.collect { contacts ->
                if (contacts.isEmpty()) {
                    if (!contactsFound) {
                        Toast.makeText(
                            context,
                            "No emergency contacts found. Please add contacts first.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    contactsFound = true
                    contacts.forEach { contact ->
                        try {
                            smsManager.sendTextMessage(
                                contact.phoneNumber,
                                null,
                                message,
                                null,
                                null
                            )
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Failed to send SMS to ${contact.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    // Show success message
                    Toast.makeText(
                        context,
                        if (isGuardianAngel)
                            "Emergency alert sent to all contacts"
                        else
                            "Live location shared with all emergency contacts",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
// Request location permission for guardian angels
fun requestLocationPermissionForGuardianAngels(context: Context) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS),
            101
        )
    } else {
        // Get the current location and share it via SMS
        getCurrentLocationForGuardianAngels(context)
    }
}

// Get the current location for guardian angels
fun getCurrentLocationForGuardianAngels(context: Context) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )

    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            shareLocationViaSMS(context, location, true)
            locationManager.removeUpdates(this)
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(context, "$provider is disabled", Toast.LENGTH_SHORT).show()
        }
    }

    try {
        for (provider in providers) {
            if (locationManager.isProviderEnabled(provider)) {
                try {
                    locationManager.requestLocationUpdates(
                        provider,
                        0L,
                        0f,
                        locationListener
                    )

                    val lastKnownLocation = locationManager.getLastKnownLocation(provider)
                    if (lastKnownLocation != null) {
                        shareLocationViaSMS(context, lastKnownLocation, true)
                        return
                    }
                } catch (e: SecurityException) {
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                    return
                } catch (e: Exception) {
                    Log.e("GuardianAngelLocation", "Error with $provider: ${e.message}")
                }
            }
        }

        Toast.makeText(context, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Location services error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun Call112Button() {
    val context = LocalContext.current
    val activity = (context as? Activity)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:112")
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Permission required to make calls", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )

    OutlinedButton(
        onClick = {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val intent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:112")
                    }
                    context.startActivity(intent)
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6E6E)),
        border = BorderStroke(2.dp, Color(0xFFFF6E6E)),
        shape = RoundedCornerShape(40.dp),
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
            .padding(end = 2.dp, start = 0.dp)
            .background(
                Color(0xFF363636),
                shape = RoundedCornerShape(40.dp)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call 112",
                    tint = Color(0xFFFF6E6E),
                    modifier = Modifier
                            .size(53.dp)
                        .padding(bottom = 15.dp),  // Adds padding on all sides
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Call 112",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 0.dp),  // Adds padding on all sides

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalSafetyScreenPreview() {
        M3BottomNavigationTheme {
            PersonalSafetyScreen()
        }
    }

class YesReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle "Yes" action
        Toast.makeText(context, "Glad you're safe!", Toast.LENGTH_SHORT).show()
    }
}

class NoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show a toast message to indicate emergency mode is activated
        Toast.makeText(context, "Emergency mode activated!", Toast.LENGTH_SHORT).show()

        // Call emergency number
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Make emergency call
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:112") // Emergency number
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }

        // Request location and send SMS to all emergency contacts
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get location and send SMS
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                shareLocationViaSMS(context, lastKnownLocation, true)
            }
        }

        // Notify nearby Guardian Angels
        requestLocationPermissionForGuardianAngels(context)
    }
}
