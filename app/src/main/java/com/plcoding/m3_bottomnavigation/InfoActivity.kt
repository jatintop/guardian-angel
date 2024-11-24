package com.plcoding.m3_bottomnavigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.plcoding.m3_bottomnavigation.ui.theme.M3BottomNavigationTheme

@OptIn(ExperimentalMaterial3Api::class)
class InfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            M3BottomNavigationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedItemIndex by rememberSaveable {
                        mutableStateOf(1)
                    }

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
                                        onClick = {
                                            selectedItemIndex = index
                                        },
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
                            when (selectedItemIndex) {
                                2 -> InfoScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen() {

    val context = LocalContext.current
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
                text = "Your Info",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1f1f1f))
                .padding(16.dp)
        ) {
            Text(
                text = "Emergency Info",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF424242)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            ) {
                EmergencyInfoButton(
                    iconResId = R.drawable.medical,
                    text = "Medical Information",
                    onClick = {
                        val intent = Intent(context, MedicalActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                )
                Divider(color = Color(0xFF2B2B2B), thickness = 2.dp)
                EmergencyInfoButton(
                    iconResId = R.drawable.emercontact,
                    text = "Emergency Contacts",
                    onClick = {
                        val intent = Intent(context, EmergencyContactsActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                )
                Divider(color = Color(0xFF2B2B2B), thickness = 2.dp)
                EmergencyInfoButton(
                    iconResId = R.drawable.locationhistory,
                    text = "Location History",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/timeline"))
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                )
            }
        }
    }
    AppInfoCard(
        onPrivacyPolicyClick = {
            val privacyIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/guardianangelprivacypolicy/home"))
            context.startActivity(privacyIntent)
        },
        onTermsOfServiceClick = {
            val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/guardian-angel-tos/home"))
            context.startActivity(termsIntent)
        }
    )
}

@Composable
fun EmergencyInfoButton(
    iconResId: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .padding(start = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            tint = Color(0xFF4FC3F7),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AppInfoCard(
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3F3F3F),
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .offset(y = 300.dp)  // This moves the card down
            .padding(
                horizontal = 30.dp,
                vertical = 64.dp
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "App Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 15.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Version",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Beta 5.0",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Release Date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "November 20, 2024",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .alpha(0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = onPrivacyPolicyClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFFFC107)
                    )
                ) {
                    Text("Privacy Policy")
                }

                TextButton(
                    onClick = onTermsOfServiceClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFFFC107)
                    )
                ) {
                    Text("Terms of Service")
                }
            }
        }
    }
}

@Composable
fun InfoScreenPreview() {
    M3BottomNavigationTheme {
        InfoScreen()
    }
}