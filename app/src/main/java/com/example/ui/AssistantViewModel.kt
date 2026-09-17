package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechController
import com.example.engine.DeterministicAssistantEngine
import com.example.model.*
import com.example.ui.components.AssistantDotState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<AssistantMessage>>(emptyList())
    val messages: StateFlow<List<AssistantMessage>> = _messages.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Float> = _audioRms.asStateFlow()

    private val _partialTranscript = MutableStateFlow("")
    val partialTranscript: StateFlow<String> = _partialTranscript.asStateFlow()

    private val _isVoiceEnabled = MutableStateFlow(true)
    val isVoiceEnabled: StateFlow<Boolean> = _isVoiceEnabled.asStateFlow()

    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteItem>>(
        listOf(
            NoteItem(text = "Buy groceries (milk, eggs, apples)", isDone = false),
            NoteItem(text = "Call dentist at 2 PM", isDone = false),
            NoteItem(text = "Finish presentation slides", isDone = true)
        )
    )
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private var speechController: SpeechController? = null

    val currentDotState: StateFlow<AssistantDotState> = combine(
        _isListening,
        _isSpeaking
    ) { listening, speaking ->
        when {
            listening -> AssistantDotState.LISTENING
            speaking -> AssistantDotState.SPEAKING
            else -> AssistantDotState.IDLE
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AssistantDotState.IDLE)

    init {
        speechController = SpeechController(
            context = application,
            onSpeechResult = { query ->
                handleUserInput(query)
            }
        )

        viewModelScope.launch {
            speechController?.isListening?.collect { listening ->
                _isListening.value = listening
            }
        }

        viewModelScope.launch {
            speechController?.isSpeaking?.collect { speaking ->
                _isSpeaking.value = speaking
            }
        }

        viewModelScope.launch {
            speechController?.speechRms?.collect { rms ->
                _audioRms.value = rms
            }
        }

        viewModelScope.launch {
            speechController?.partialText?.collect { text ->
                _partialTranscript.value = text
            }
        }

        // Add initial welcome message with daily briefing & suggestions
        sendWelcomeMessage()
    }

    private fun sendWelcomeMessage() {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour in 4..11 -> "Good morning!"
            hour in 12..16 -> "Good afternoon!"
            else -> "Good evening!"
        }
        val dateText = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

        val welcomeMsg = AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = "Hi, how can I help?",
            spokenText = "$greeting Hi, how can I help you today?",
            card = AssistantCardData.DailyBriefing(
                greeting = greeting,
                dateText = dateText,
                weatherSummary = "22°C, Partly Cloudy in New York",
                quote = "The secret of getting ahead is getting started.",
                quoteAuthor = "Mark Twain",
                reminderSummary = "3 tasks on your list",
                batteryLevel = 85
            ),
            suggestionChips = listOf(
                "Select default assistant",
                "What's the weather?",
                "Set a 5m timer",
                "Daily briefing",
                "Flip a coin",
                "Calculate 15% of 85",
                "Tell me a joke"
            )
        )
        _messages.value = listOf(welcomeMsg)
    }

    fun handleUserInput(rawQuery: String) {
        if (rawQuery.isBlank()) return

        val userMessage = AssistantMessage(
            sender = MessageSender.USER,
            text = rawQuery.trim()
        )

        // Process query through deterministic rule engine
        val response = DeterministicAssistantEngine.processQuery(
            rawQuery = rawQuery,
            currentNotes = _notes.value,
            isFlashlightOn = _isFlashlightOn.value
        )

        _messages.update { current -> current + userMessage + response }

        // Trigger voice readout if enabled
        if (_isVoiceEnabled.value && response.spokenText.isNotBlank()) {
            speechController?.speak(response.spokenText)
        }
    }

    fun toggleListening() {
        if (_isListening.value) {
            speechController?.stopListening()
        } else {
            speechController?.startListening()
        }
    }

    fun stopSpeaking() {
        speechController?.stopSpeaking()
    }

    fun toggleVoiceFeedback() {
        val next = !_isVoiceEnabled.value
        _isVoiceEnabled.value = next
        speechController?.isTtsEnabled = next
        if (!next) {
            speechController?.stopSpeaking()
        }
    }

    fun clearHistory() {
        speechController?.stopSpeaking()
        speechController?.stopListening()
        sendWelcomeMessage()
    }

    override fun onCleared() {
        super.onCleared()
        speechController?.destroy()
    }
}
