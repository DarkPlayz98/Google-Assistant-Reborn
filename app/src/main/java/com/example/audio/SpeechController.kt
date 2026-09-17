package com.example.audio

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechController(
    private val context: Context,
    private val onSpeechResult: (String) -> Unit
) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var toneGenerator: ToneGenerator? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechRms = MutableStateFlow(0f)
    val speechRms: StateFlow<Float> = _speechRms.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    var isTtsEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
        } catch (e: Exception) {
            Log.e("SpeechController", "ToneGenerator init failed", e)
        }
        initSpeechRecognizer()
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@SpeechController)
            }
        }
    }

    fun startListening() {
        stopSpeaking()
        playStartTone()
        _partialText.value = ""
        _isListening.value = true

        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechController", "Error starting listening", e)
            _isListening.value = false
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechController", "Error stopping listening", e)
        }
        _isListening.value = false
        _speechRms.value = 0f
    }

    fun speak(text: String) {
        if (!isTtsEnabled || !isTtsInitialized || text.isBlank()) return
        stopListening()
        _isSpeaking.value = true
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AssistantUtteranceId")
    }

    fun stopSpeaking() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
        _isSpeaking.value = false
    }

    private fun playStartTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
        } catch (e: Exception) {
            Log.e("SpeechController", "Error playing tone", e)
        }
    }

    fun playEndTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        } catch (e: Exception) {
            Log.e("SpeechController", "Error playing tone", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            isTtsInitialized = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            textToSpeech?.setSpeechRate(1.02f)
            textToSpeech?.setPitch(1.0f)
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _isListening.value = true
    }

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {
        _speechRms.value = (rmsdB / 10f).coerceIn(0f, 1f)
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        _isListening.value = false
        _speechRms.value = 0f
    }

    override fun onError(error: Int) {
        _isListening.value = false
        _speechRms.value = 0f
        _partialText.value = ""
    }

    override fun onResults(results: Bundle?) {
        _isListening.value = false
        _speechRms.value = 0f
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val spokenQuery = matches[0]
            _partialText.value = ""
            onSpeechResult(spokenQuery)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            _partialText.value = matches[0]
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("SpeechController", "Cleanup error", e)
        }
    }
}
