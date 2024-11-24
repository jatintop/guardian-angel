package com.plcoding.m3_bottomnavigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import androidx.room.Delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

class ActHistory : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContent {
                ActivityHistoryScreen()
            }
        } catch (e: Exception) {
            Log.e("ActHistory", "Error creating activity", e)
            // Optional: show an error toast
            Toast.makeText(this, "Error loading activity history: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
// Entity representing an activity record
@Entity(tableName = "activity_history")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityName: String,
    val duration: String,
    val date: Long,
    val safetyStatus: SafetyStatus
)

// Enum for safety status
enum class SafetyStatus {
    SAFE, MODERATE_RISK, HIGH_RISK
}

// Data Access Object (DAO) for activity records
@Dao
interface ActivityRecordDao {
    @Query("SELECT * FROM activity_history ORDER BY date DESC")
    fun getAllActivities(): List<ActivityRecord>

    @Insert
    fun insertActivity(activityRecord: ActivityRecord)

    @Delete
    fun deleteActivity(activityRecord: ActivityRecord)

    @Query("DELETE FROM activity_history")
    fun clearAllActivities()
}

@Database(entities = [ActivityRecord::class], version = 1)
abstract class ActivityDatabase : RoomDatabase() {
    abstract fun activityRecordDao(): ActivityRecordDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityDatabase? = null

        fun getInstance(context: Context): ActivityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityDatabase::class.java,
                    "activity_history_database"
                )
                    .fallbackToDestructiveMigration() // This will recreate the database if there are version conflicts
                    .allowMainThreadQueries() // For debugging - remove in production
                    .build()
                INSTANCE = instance
                Log.d("ActivityDatabase", "Database instance created")
                instance
            }
        }
    }
}

// Utility function to determine safety status based on activity
fun determineSafetyStatus(activityName: String, duration: String): SafetyStatus {
    return when {
        activityName.contains("Hiking", ignoreCase = true) && duration.contains("hour", ignoreCase = true) -> SafetyStatus.MODERATE_RISK
        activityName.contains("Taking Transportation", ignoreCase = true) && duration.contains("hour", ignoreCase = true) -> SafetyStatus.SAFE
        activityName.contains("Walking Alone", ignoreCase = true) && duration.contains("hour", ignoreCase = true) -> SafetyStatus.HIGH_RISK
        else -> SafetyStatus.SAFE
    }
}

class ActivityHistoryViewModel(private val database: ActivityDatabase) : ViewModel() {
    private val _activities = MutableStateFlow<List<ActivityRecord>>(emptyList())
    val activities: StateFlow<List<ActivityRecord>> = _activities.asStateFlow()

    init {
        viewModelScope.launch {
            _activities.value = withContext(Dispatchers.IO) {
                database.activityRecordDao().getAllActivities()
            }
        }
    }

    fun deleteActivity(activity: ActivityRecord) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.activityRecordDao().deleteActivity(activity)
            }
            _activities.value = withContext(Dispatchers.IO) {
                database.activityRecordDao().getAllActivities()
            }
        }
    }

    fun clearAllActivities() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.activityRecordDao().clearAllActivities()
            }
            _activities.value = emptyList()
        }
    }
}


// Composable for displaying activity history
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHistoryScreen() {
    val context = LocalContext.current
    val viewModel: ActivityHistoryViewModel = viewModel {
        ActivityHistoryViewModel(ActivityDatabase.getInstance(context))
    }
    val activities by viewModel.activities.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity History", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3F3F3F)
                )
            )
        },
        containerColor = Color(0xFF2C2C2C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (activities.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No activity history. Try starting an activity and check again.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    item {
                        Button(
                            onClick = { viewModel.clearAllActivities() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7C544))
                        ) {
                            Text("Clear History", color = Color.Black)
                        }
                    }

                    items(activities) { activity ->
                        ActivityHistoryItem(
                            activity = activity,
                            onDelete = { viewModel.deleteActivity(activity) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityHistoryItem(
    activity: ActivityRecord,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(activity.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF424242))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.activityName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Date: $formattedDate",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Duration: ${activity.duration}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Safety: ${activity.safetyStatus}",
                    color = when (activity.safetyStatus) {
                        SafetyStatus.SAFE -> Color.Green
                        SafetyStatus.MODERATE_RISK -> Color.Yellow
                        SafetyStatus.HIGH_RISK -> Color.Red
                    },
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Activity",
                    tint = Color.White
                )
            }
        }
    }
}

// Extension function to save activity to history
// Enhanced save function with more robust logging
fun saveActivityToHistory(
    context: Context,
    activityName: String,
    duration: String
) {
    try {
        val database = ActivityDatabase.getInstance(context)
        val safetyStatus = determineSafetyStatus(activityName, duration)

        val activityRecord = ActivityRecord(
            activityName = activityName,
            duration = duration,
            date = System.currentTimeMillis(),
            safetyStatus = safetyStatus
        )

        // Use runBlocking for guaranteed synchronous execution (for debugging)
        runBlocking {
            try {
                database.activityRecordDao().insertActivity(activityRecord)
                Log.d("ActivityHistory", "Activity DEFINITELY saved: $activityName, Duration: $duration")

                val activities = database.activityRecordDao().getAllActivities()
                Log.d("ActivityHistory", "TOTAL ACTIVITIES: ${activities.size}")
                activities.forEach { activity ->
                    Log.d("ActivityHistory", "SAVED ACTIVITY DETAILS: ${activity.activityName}, ${activity.duration}, ${activity.date}")
                }
            } catch (e: Exception) {
                Log.e("ActivityHistory", "INSERTION ERROR", e)
            }
        }
    } catch (e: Exception) {
        Log.e("ActivityHistory", "CATASTROPHIC ERROR", e)
    }
}