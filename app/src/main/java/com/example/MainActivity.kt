package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ui.AssistantScreen
import com.example.ui.AssistantViewModel
import com.example.ui.theme.AssistantTheme

class MainActivity : ComponentActivity() {

    private val assistantViewModel: AssistantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleAssistIntent(intent)

        setContent {
            AssistantTheme {
                AssistantScreen(viewModel = assistantViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAssistIntent(intent)
    }

    private fun handleAssistIntent(intent: Intent?) {
        if (intent == null) return

        val isAssistAction = intent.action == Intent.ACTION_ASSIST ||
                intent.action == Intent.ACTION_VOICE_COMMAND ||
                intent.action == "android.intent.action.VOICE_ASSIST" ||
                intent.getBooleanExtra("EXTRA_START_VOICE_LISTENING", false)

        if (isAssistAction) {
            val hasRecordPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasRecordPermission) {
                assistantViewModel.startListening()
            }
        }
    }
}


