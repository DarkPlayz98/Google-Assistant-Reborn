package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AssistantScreen
import com.example.ui.AssistantViewModel
import com.example.ui.theme.AssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssistantTheme {
                val assistantViewModel: AssistantViewModel = viewModel()
                AssistantScreen(viewModel = assistantViewModel)
            }
        }
    }
}

