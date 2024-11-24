package com.plcoding.m3_bottomnavigation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.vector.ImageVector
import com.plcoding.m3_bottomnavigation.ui.theme.M3BottomNavigationTheme

class FeaturesActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            M3BottomNavigationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FeaturesActivityContent(
                        onNavigateToGemini = {

                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesActivityContent(onNavigateToGemini: () -> Unit) {
    var selectedItemIndex by remember { mutableStateOf(1) }

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Features",
            selectedIcon = Icons.Filled.Lock,
            unselectedIcon = Icons.Outlined.Lock,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Your Info",
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            hasNews = false,
        ),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        label = {
                            Text(text = item.title)
                        },
                        alwaysShowLabel = true,
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            FeaturesScreen()
        }
    }
}

@Composable
fun FeaturesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1f1f1f))
            .padding(
                start = 16.dp,
                top = 32.dp,
                end = 16.dp,
                bottom = 0.dp
            )
    ) {
        // Top Row with Icons and Title
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
                text = "Features",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Sound Alert Card
            FeatureCard(
                title = "Loud Sound Alert",
                description = "Plays a loud sound to alert people nearby of any danger that you might encounter",
                iconId = R.drawable.sound
            ) {
                SoundAlertButton()
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animal Sound Alert Card
            FeatureCard(
                title = "Animal Sound Alert",
                description = "Plays a harmless ultrasonic sound to scare away nearby animals",
                iconId = R.drawable.animal
            ) {
                AnimalSoundAlertButton()
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animal Sound Alert Card
            FeatureCard(
                title = "Crisis Alert",
                description = "Get notified about natural disasters and public emergencies affecting your area",
                iconId = R.drawable.crisis
            ) {
                CrisisAlertButton()
            }
        }
    }
}

@Composable
fun CrisisAlertButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ECBEE)),
        shape = RoundedCornerShape(50),
        modifier = Modifier.width(120.dp)
    ) {
        Text(
            text = "Enroll",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SoundAlertButton() {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (isPlaying) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            } else {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(context, R.raw.loudahhsound).apply {
                    start()
                }
            }
            isPlaying = !isPlaying
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ECBEE)),
        shape = RoundedCornerShape(50),
        modifier = Modifier.width(120.dp)
    ) {
        Text(
            text = if (isPlaying) "Stop" else "Play",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AnimalSoundAlertButton() {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (isPlaying) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            } else {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(context, R.raw.animalalert).apply {
                    start()
                }
            }
            isPlaying = !isPlaying
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5ECBEE)),
        shape = RoundedCornerShape(50),
        modifier = Modifier.width(120.dp)
    ) {
        Text(
            text = if (isPlaying) "Stop" else "Play",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    iconId: Int,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3F3F3F)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = "$title Icon",
                    tint = Color(0xFF5ECBEE),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(23.dp))
            Text(
                text = description,
                color = Color(0xFFFFFFFF),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(23.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                content()
            }
        }
    }
}

