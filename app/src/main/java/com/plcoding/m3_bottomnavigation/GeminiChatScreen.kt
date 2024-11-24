package com.plcoding.m3_bottomnavigation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.GenerativeModel
import android.os.Bundle
import android.view.TextureView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Send
import com.plcoding.m3_bottomnavigation.ui.theme.M3BottomNavigationTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.unit.Dp
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import kotlinx.coroutines.delay


@Composable
fun GeminiChatContent() {
    val viewModel: GeminiViewModel = viewModel()
    val conversation by viewModel.conversation.collectAsState()
    val userInput by viewModel.userInput.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1f1f1f))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            if (conversation.isEmpty() && !isThinking) {
                WelcomeMessage()
                SuggestionPrompts(onSuggestionClick = { suggestion ->
                    viewModel.updateUserInput(suggestion)
                })
            } else {
                ChatMessages(conversation)
                if (isThinking) {
                    ThinkingIndicator()
                }
            }
        }

        UserInputRow(
            userInput = userInput,
            onUserInputChange = { viewModel.updateUserInput(it) },
            onSendClick = {
                Log.d("GeminiChatScreen", "Send button clicked")
                viewModel.sendMessage()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ThinkingIndicator() {
    val dotCount = 3
    var animationState by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(100) // Faster animation cycle
            animationState = (animationState + 1) % (dotCount * 4) // More granular steps
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3C3C3C))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) { index ->
                    ThinkingDot(
                        offset = calculateDotOffset(index, animationState, dotCount)
                    )
                    if (index < dotCount - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ThinkingDot(offset: Dp) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .offset(y = offset)
            .background(Color.White, CircleShape)
    )
}

fun calculateDotOffset(dotIndex: Int, animationState: Int, dotCount: Int): Dp {
    val dotAnimationIndex = (animationState - dotIndex * 4 + dotCount * 4) % (dotCount * 4)
    return when {
        dotAnimationIndex < 4 -> (-4 * dotAnimationIndex).dp
        dotAnimationIndex < 8 -> ((-16 + 4 * (dotAnimationIndex - 4))).dp
        else -> 0.dp
    }
}

@Composable
fun WelcomeMessage() {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF54B1E4), // Blue
            Color(0xFF9B4CBD), // Purple
            Color(0xFFFA695C)  // Pink
        )
    )

    Text(
        text = "Hi I'm Gemini,",
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        style = TextStyle(
            brush = gradientBrush
        ),
        modifier = Modifier.padding(vertical = 20.dp)
    )
}

@Composable
fun SuggestionPrompts(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf(
        "Prepare for hurricane",
        "Emergency kit essentials",
        "Flood evacuation steps",
        "Earthquake safety tips",
        "Wildfire preparedness",
        "First aid basics"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        suggestions.chunked(2).forEach { rowSuggestions ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 21.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowSuggestions.forEach { suggestion ->
                    SuggestionBox(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // If there's only one suggestion in the row, add an empty box for spacing
                if (rowSuggestions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SuggestionBox(
    suggestion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(end = 8.dp, start = 1.dp)
            .height(80.dp)
            .width(100.dp)
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF424242))
            .clickable(onClick = onClick)
            .padding(11.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = suggestion,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            lineHeight = 18.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ChatMessages(conversation: List<ChatMessage>) {
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 8.dp),
        reverseLayout = true
    ) {
        items(conversation.reversed()) { message ->
            MessageBubble(message)
        }
    }
}

@Composable
fun UserInputRow(
    userInput: String,
    onUserInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between TextField and Button
    ) {
        TextField(
            value = userInput,
            onValueChange = { onUserInputChange(it) },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(34.dp)),
            placeholder = { Text("Enter your prompt") },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color(0xFF333333),  // Dark gray hex
                focusedContainerColor = Color(0xFF333333),   // Darker gray hex
                unfocusedContainerColor = Color(0xFF333333)  // Lighter dark gray hex
            ),
            singleLine = true
        )
        Box(
            modifier = Modifier
                .size(53.dp) // Adjust size as needed
                .clip(RoundedCornerShape(34.dp)) // More rounded
                .background(Color(0xFFFFC107)) // Yellow background
                .clickable { onSendClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = "Send",
                tint = Color.Black, // Ensure the icon color contrasts well with the button background
                modifier = Modifier.size(28.dp) // Adjust size as needed
            )
        }
    }
}



@Composable
fun MessageBubble(message: ChatMessage) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3C3C3C)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "${message.sender}: ${message.content}",
            modifier = Modifier.padding(8.dp),
            color = Color.White
        )
    }
}