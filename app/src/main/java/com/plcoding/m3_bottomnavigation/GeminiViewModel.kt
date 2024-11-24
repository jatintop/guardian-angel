package com.plcoding.m3_bottomnavigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.plcoding.m3_bottomnavigation.BuildConfig  // Use your app's package name

class GeminiViewModel : ViewModel() {
    private val _conversation = MutableStateFlow<List<ChatMessage>>(emptyList())
    val conversation: StateFlow<List<ChatMessage>> = _conversation.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun updateUserInput(input: String) {
        _userInput.value = input
    }

    fun sendMessage() {
        _isThinking.value = true
        val userMessage = _userInput.value
        if (userMessage.isNotBlank()) {
            _conversation.value += ChatMessage("User", userMessage)
            _userInput.value = ""

            viewModelScope.launch {
                try {
                    Log.d("GeminiViewModel", "Sending message: $userMessage") // Log statement
                    val response = generativeModel.generateContent(userMessage)
                    _conversation.value += ChatMessage("Gemini", response.text ?: "No response")
                } catch (e: Exception) {
                    _conversation.value += ChatMessage("Gemini", "Error: ${e.message}")
                }
                _isThinking.value = false
            }
        }
    }
}

data class ChatMessage(val sender: String, val content: String)