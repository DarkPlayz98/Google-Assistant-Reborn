package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.components.AssistantCardContainer
import com.example.ui.components.AssistantDotState
import com.example.ui.components.AssistantDotsWave
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    viewModel: AssistantViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val audioRms by viewModel.audioRms.collectAsStateWithLifecycle()
    val partialTranscript by viewModel.partialTranscript.collectAsStateWithLifecycle()
    val isVoiceEnabled by viewModel.isVoiceEnabled.collectAsStateWithLifecycle()
    val dotState by viewModel.currentDotState.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    var isKeyboardMode by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Permission launcher for microphone
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleListening()
        }
    }

    fun onMicClicked() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.toggleListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Auto scroll down when new messages arrive
    LaunchedEffect(messages.size, partialTranscript) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("assistant_main_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 4-Dots icon
                        AssistantDotsWave(
                            state = dotState,
                            audioRms = audioRms,
                            dotSize = 7.dp,
                            spacing = 4.dp
                        )
                        Text(
                            text = "Assistant",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Select Default Assistant button
                    IconButton(
                        onClick = { viewModel.handleUserInput("select default assistant") },
                        modifier = Modifier.testTag("default_assistant_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsVoice,
                            contentDescription = "Select Default Assistant",
                            tint = AssistantBlue
                        )
                    }

                    // Voice feedback toggle
                    IconButton(
                        onClick = { viewModel.toggleVoiceFeedback() },
                        modifier = Modifier.testTag("voice_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isVoiceEnabled) "Voice enabled" else "Voice muted",
                            tint = if (isVoiceEnabled) AssistantBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Help button
                    IconButton(
                        onClick = { showHelpDialog = true },
                        modifier = Modifier.testTag("help_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = "Help & Commands",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Clear conversation button
                    IconButton(
                        onClick = { viewModel.clearHistory() },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = "Clear conversation",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 12.dp)
            ) {
                // Horizontal Suggestion Chips Carousel
                val suggestionList = messages.lastOrNull()?.suggestionChips?.ifEmpty { null }
                    ?: listOf(
                        "Select default assistant",
                        "What's the weather?",
                        "Set a 5m timer",
                        "Daily briefing",
                        "Flip a coin",
                        "Roll a dice",
                        "Calculate 15% of 85",
                        "Tell me a joke",
                        "Convert 100 USD to EUR",
                        "Flashlight on"
                    )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestionList.forEach { chipText ->
                        SuggestionChip(
                            onClick = {
                                viewModel.handleUserInput(chipText)
                            },
                            label = {
                                Text(
                                    text = chipText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = null,
                            modifier = Modifier.testTag("suggestion_chip_$chipText")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Floating Input Bar or Voice Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shadowElevation = 3.dp
                ) {
                    AnimatedContent(
                        targetState = isKeyboardMode,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "input_mode_switch"
                    ) { keyboardActive ->
                        if (keyboardActive) {
                            // Text Input Field Mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { isKeyboardMode = false },
                                    modifier = Modifier.testTag("switch_to_voice_mode")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Switch to voice",
                                        tint = AssistantBlue
                                    )
                                }

                                TextField(
                                    value = textInput,
                                    onValueChange = { textInput = it },
                                    placeholder = {
                                        Text(
                                            "Ask me anything...",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("text_input_field"),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                    keyboardActions = KeyboardActions(
                                        onSend = {
                                            if (textInput.isNotBlank()) {
                                                viewModel.handleUserInput(textInput)
                                                textInput = ""
                                                focusManager.clearFocus()
                                            }
                                        }
                                    )
                                )

                                IconButton(
                                    onClick = {
                                        if (textInput.isNotBlank()) {
                                            viewModel.handleUserInput(textInput)
                                            textInput = ""
                                            focusManager.clearFocus()
                                        }
                                    },
                                    modifier = Modifier.testTag("send_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = if (textInput.isNotBlank()) AssistantBlue else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        } else {
                            // Classic Voice Assistant Bottom Bar Mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Keyboard Switch Button
                                IconButton(
                                    onClick = { isKeyboardMode = true },
                                    modifier = Modifier.testTag("switch_to_keyboard_mode")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Keyboard,
                                        contentDescription = "Keyboard input",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Center 4-Dots / Live Wave Status
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onMicClicked() }
                                        .padding(horizontal = 8.dp)
                                ) {
                                    AssistantDotsWave(
                                        state = dotState,
                                        audioRms = audioRms,
                                        dotSize = 10.dp,
                                        spacing = 8.dp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when {
                                            isListening -> if (partialTranscript.isNotBlank()) "\"$partialTranscript\"" else "Listening..."
                                            isSpeaking -> "Speaking..."
                                            else -> "Tap mic to speak"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = when {
                                            isListening -> AssistantBlue
                                            isSpeaking -> AssistantGreen
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        maxLines = 1
                                    )
                                }

                                // Glowing Animated Mic Button
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isListening) {
                                                Brush.radialGradient(
                                                    colors = listOf(AssistantRed, Color(0xFFD93025))
                                                )
                                            } else {
                                                Brush.radialGradient(
                                                    colors = listOf(AssistantBlue, Color(0xFF1967D2))
                                                )
                                            }
                                        )
                                        .clickable { onMicClicked() }
                                        .testTag("mic_action_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = if (isListening) "Stop listening" else "Start listening",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                AssistantMessageItem(
                    message = message,
                    onExecuteAction = { query -> viewModel.handleUserInput(query) }
                )
            }
        }
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistantDotsWave(state = AssistantDotState.IDLE, dotSize = 8.dp, spacing = 5.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Classic Assistant Guide", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "This app reproduces the iconic Google Assistant experience completely offline with zero AI! All commands are processed deterministically with native voice & utilities.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text("Popular Commands:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("• \"Select default assistant\" (Configure system default)", style = MaterialTheme.typography.bodySmall)
                    Text("• \"What's the weather in New York?\"", style = MaterialTheme.typography.bodySmall)
                    Text("• \"Set a 5 minute timer\"", style = MaterialTheme.typography.bodySmall)
                    Text("• \"Calculate 25% of 80\"", style = MaterialTheme.typography.bodySmall)
                    Text("• \"Convert 100 USD to EUR\"", style = MaterialTheme.typography.bodySmall)
                    Text("• \"Flip a coin\" / \"Roll a dice\"", style = MaterialTheme.typography.bodySmall)
                    Text("• \"Tell me a joke\"", style = MaterialTheme.typography.bodySmall)
                    Text("• \"Turn on flashlight\"", style = MaterialTheme.typography.bodySmall)
                    Text("• \"Daily briefing\"", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun AssistantMessageItem(
    message: AssistantMessage,
    onExecuteAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (message.sender == MessageSender.USER) {
        // User Message Bubble
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 6.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    } else {
        // Assistant Message Bubble & Rich Card
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Assistant Text Intro
            if (message.text.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        AssistantDotsWave(
                            state = AssistantDotState.IDLE,
                            dotSize = 3.dp,
                            spacing = 2.dp
                        )
                    }
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Rich Card (if present)
            if (message.card != null) {
                AssistantCardContainer(
                    card = message.card,
                    onExecuteAction = onExecuteAction,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
