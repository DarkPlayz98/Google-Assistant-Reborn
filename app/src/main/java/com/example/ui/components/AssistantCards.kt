package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun AssistantCardContainer(
    card: AssistantCardData,
    onExecuteAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (card) {
        is AssistantCardData.Weather -> WeatherCard(card = card, onExecuteAction = onExecuteAction, modifier = modifier)
        is AssistantCardData.Timer -> TimerCard(initialTimer = card, modifier = modifier)
        is AssistantCardData.Alarm -> AlarmCard(alarm = card, modifier = modifier)
        is AssistantCardData.MathResult -> MathResultCard(card = card, modifier = modifier)
        is AssistantCardData.UnitConversion -> UnitConversionCard(card = card, modifier = modifier)
        is AssistantCardData.DailyBriefing -> DailyBriefingCard(card = card, onExecuteAction = onExecuteAction, modifier = modifier)
        is AssistantCardData.DeviceControl -> DeviceControlCard(card = card, modifier = modifier)
        is AssistantCardData.CoinFlip -> CoinFlipCard(card = card, onFlipAgain = { onExecuteAction("flip a coin") }, modifier = modifier)
        is AssistantCardData.DiceRoll -> DiceRollCard(card = card, onRollAgain = { onExecuteAction("roll a dice") }, modifier = modifier)
        is AssistantCardData.RockPaperScissors -> RockPaperScissorsCard(card = card, onPlayChoice = { onExecuteAction(it) }, modifier = modifier)
        is AssistantCardData.Joke -> JokeCard(card = card, onAnotherJoke = { onExecuteAction("tell me a joke") }, modifier = modifier)
        is AssistantCardData.NoteReminder -> NoteReminderCard(card = card, modifier = modifier)
        is AssistantCardData.WebSearch -> WebSearchCard(card = card, modifier = modifier)
        is AssistantCardData.CapabilitiesHelp -> CapabilitiesHelpCard(card = card, onQuerySelect = onExecuteAction, modifier = modifier)
        is AssistantCardData.CrystalBall -> CrystalBallCard(card = card, onAskAgain = { onExecuteAction("magic 8 ball") }, modifier = modifier)
        is AssistantCardData.AnimalSound -> AnimalSoundCard(card = card, onAnimalSelect = onExecuteAction, modifier = modifier)
        is AssistantCardData.DefaultAssistantSetting -> DefaultAssistantSettingCard(card = card, modifier = modifier)
    }
}

// 1. Weather Card
@Composable
fun WeatherCard(
    card: AssistantCardData.Weather,
    onExecuteAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = card.city,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = card.condition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(text = card.conditionIcon, fontSize = 44.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${card.tempC}°C",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${card.tempF}°F",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "H: ${card.highTemp}°  L: ${card.lowTemp}°", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Humidity: ${card.humidity}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Hourly row
            Text(
                text = "HOURLY FORECAST",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                card.hourlyForecast.take(5).forEach { hour ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = hour.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = hour.icon, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${hour.tempC}°", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                    }
                }
            }
        }
    }
}

// 2. Interactive Live Timer Card
@Composable
fun TimerCard(
    initialTimer: AssistantCardData.Timer,
    modifier: Modifier = Modifier
) {
    var remainingSeconds by remember { mutableIntStateOf(initialTimer.remainingSeconds) }
    var isRunning by remember { mutableStateOf(initialTimer.isRunning) }
    var totalSeconds by remember { mutableIntStateOf(initialTimer.totalSeconds) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        } else if (remainingSeconds <= 0) {
            isRunning = false
        }
    }

    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = AssistantBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = initialTimer.label,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                if (remainingSeconds <= 0) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Done! 🔔", color = AssistantRed) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = AssistantRed.copy(alpha = 0.1f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time Display with Progress Bar
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = if (remainingSeconds <= 0) AssistantRed else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progress < 0.2f) AssistantRed else AssistantBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // +1 Min button
                OutlinedButton(
                    onClick = {
                        remainingSeconds += 60
                        totalSeconds += 60
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("+1 min")
                }

                // Play / Pause FAB
                FilledTonalIconButton(
                    onClick = {
                        if (remainingSeconds <= 0) {
                            remainingSeconds = totalSeconds
                        }
                        isRunning = !isRunning
                    },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (isRunning) AssistantYellow.copy(alpha = 0.2f) else AssistantBlue.copy(alpha = 0.2f),
                        contentColor = if (isRunning) AssistantYellow else AssistantBlue
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play",
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Reset Button
                OutlinedButton(
                    onClick = {
                        isRunning = false
                        remainingSeconds = totalSeconds
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}

// 3. Alarm Card
@Composable
fun AlarmCard(
    alarm: AssistantCardData.Alarm,
    modifier: Modifier = Modifier
) {
    var isEnabled by remember { mutableStateOf(alarm.isEnabled) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = AssistantBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alarm.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alarm.timeFormatted,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = alarm.days,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AssistantBlue)
            )
        }
    }
}

// 4. Math Result Card
@Composable
fun MathResultCard(
    card: AssistantCardData.MathResult,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(Icons.Default.Calculate, contentDescription = null, tint = AssistantBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = card.expression,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "= ${card.result}",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// 5. Unit Conversion Card
@Composable
fun UnitConversionCard(
    card: AssistantCardData.UnitConversion,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "CONVERSION",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "${card.fromValue}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = card.fromUnit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = AssistantBlue, modifier = Modifier.size(32.dp))
                Column(horizontalAlignment = Alignment.End) {
                    val formatted = String.format(Locale.US, "%.2f", card.toValue)
                    Text(text = formatted, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Text(text = card.toUnit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Formula: ${card.formula}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// 6. Daily Briefing ("My Day") Card
@Composable
fun DailyBriefingCard(
    card: AssistantCardData.DailyBriefing,
    onExecuteAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY BRIEFING",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = AssistantBlue
                )
                Text(text = "🔋 ${card.batteryLevel}%", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = card.dateText,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Weather Snippet
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExecuteAction("what's the weather") }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⛅", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = card.weatherSummary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tasks / Reminders Snippet
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExecuteAction("show my reminders") }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📋", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = card.reminderSummary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quote of the day
            Text(
                text = "“${card.quote}”",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "— ${card.quoteAuthor}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// 7. Device Controls Card (Flashlight, Volume)
@Composable
fun DeviceControlCard(
    card: AssistantCardData.DeviceControl,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFlashlightOn by remember { mutableStateOf(card.flashlightOn) }
    var volumePct by remember { mutableFloatStateOf(card.volumePercent.toFloat()) }

    fun toggleFlashlight(enable: Boolean) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, enable)
                isFlashlightOn = enable
            } else {
                isFlashlightOn = enable
            }
        } catch (e: Exception) {
            isFlashlightOn = enable
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "DEVICE CONTROLS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Flashlight Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        contentDescription = null,
                        tint = if (isFlashlightOn) AssistantYellow else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Flashlight",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                FilledTonalButton(
                    onClick = { toggleFlashlight(!isFlashlightOn) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isFlashlightOn) AssistantYellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(if (isFlashlightOn) "ON" else "OFF", color = if (isFlashlightOn) AssistantYellow else MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Volume Row
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Media Volume", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${volumePct.toInt()}%", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
                Slider(
                    value = volumePct,
                    onValueChange = { volumePct = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = AssistantBlue,
                        activeTrackColor = AssistantBlue
                    )
                )
            }
        }
    }
}

// 8. Coin Flip Card with 3D animation
@Composable
fun CoinFlipCard(
    card: AssistantCardData.CoinFlip,
    onFlipAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipping by remember { mutableStateOf(false) }
    var resultHeads by remember { mutableStateOf(card.isHeads) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipping) 720f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        finishedListener = {
            isFlipping = false
        },
        label = "coin_flip"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "COIN FLIP",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = AssistantBlue
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3D flipping coin circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AssistantYellow, Color(0xFFE5A100))
                        )
                    )
                    .clickable {
                        isFlipping = true
                        resultHeads = kotlin.random.Random.nextBoolean()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (resultHeads) "HEADS" else "TAILS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Result: ${if (resultHeads) "Heads" else "Tails"}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    isFlipping = true
                    resultHeads = kotlin.random.Random.nextBoolean()
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Flip Again")
            }
        }
    }
}

// 9. Dice Roll Card
@Composable
fun DiceRollCard(
    card: AssistantCardData.DiceRoll,
    onRollAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rollValue by remember { mutableIntStateOf(card.rollValue) }
    var isRolling by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isRolling) 360f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        finishedListener = { isRolling = false },
        label = "dice_roll"
    )

    val diceDots = when (rollValue) {
        1 -> "⚀"
        2 -> "⚁"
        3 -> "⚂"
        4 -> "⚃"
        5 -> "⚄"
        6 -> "⚅"
        else -> rollValue.toString()
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DICE ROLL",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = AssistantRed
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer { rotationZ = rotation }
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        isRolling = true
                        rollValue = kotlin.random.Random.nextInt(1, 7)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = diceDots, fontSize = 56.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Rolled a $rollValue",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    isRolling = true
                    rollValue = kotlin.random.Random.nextInt(1, 7)
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Roll Again")
            }
        }
    }
}

// 10. Rock Paper Scissors Card
@Composable
fun RockPaperScissorsCard(
    card: AssistantCardData.RockPaperScissors,
    onPlayChoice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ROCK PAPER SCISSORS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = AssistantGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "You", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = when (card.userChoice) {
                            "Rock" -> "✊"
                            "Paper" -> "✋"
                            else -> "✌️"
                        },
                        fontSize = 44.sp
                    )
                    Text(text = card.userChoice, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }

                Text(text = "VS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = MaterialTheme.colorScheme.outline)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Assistant", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = when (card.assistantChoice) {
                            "Rock" -> "✊"
                            "Paper" -> "✋"
                            else -> "✌️"
                        },
                        fontSize = 44.sp
                    )
                    Text(text = card.assistantChoice, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = card.outcome,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = if (card.outcome.contains("win")) AssistantGreen else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AssistChip(onClick = { onPlayChoice("rock") }, label = { Text("✊ Rock") })
                AssistChip(onClick = { onPlayChoice("paper") }, label = { Text("✋ Paper") })
                AssistChip(onClick = { onPlayChoice("scissors") }, label = { Text("✌️ Scissors") })
            }
        }
    }
}

// 11. Joke Card
@Composable
fun JokeCard(
    card: AssistantCardData.Joke,
    onAnotherJoke: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FUN & HUMOR",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = AssistantYellow
                )
                Text(text = "😂", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = card.setup,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { revealed = !revealed }
            ) {
                Box(
                    modifier = Modifier.padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (revealed) {
                        Text(
                            text = card.punchline,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = AssistantBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tap to reveal punchline",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onAnotherJoke) {
                    Text("Tell another joke")
                }
            }
        }
    }
}

// 12. Note / Reminder List Card
@Composable
fun NoteReminderCard(
    card: AssistantCardData.NoteReminder,
    modifier: Modifier = Modifier
) {
    val checkedStates = remember { mutableStateMapOf<Int, Boolean>() }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Checklist, contentDescription = null, tint = AssistantBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = card.timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            card.items.forEachIndexed { index, itemText ->
                val isChecked = checkedStates[index] ?: false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { checkedStates[index] = !isChecked }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checkedStates[index] = it },
                        colors = CheckboxDefaults.colors(checkedColor = AssistantBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = itemText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                            color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

// 13. Web Search Intent Card
@Composable
fun WebSearchCard(
    card: AssistantCardData.WebSearch,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SEARCH RESULTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(Icons.Default.Language, contentDescription = null, tint = AssistantBlue)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = card.query,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = card.snippet,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(card.url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search on Google")
            }
        }
    }
}

// 14. Capabilities Help Card
@Composable
fun CapabilitiesHelpCard(
    card: AssistantCardData.CapabilitiesHelp,
    onQuerySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "WHAT YOU CAN DO",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = AssistantBlue
            )
            Spacer(modifier = Modifier.height(14.dp))

            card.categories.forEach { cat ->
                Text(
                    text = "${cat.icon} ${cat.title}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cat.sampleQueries.forEach { sample ->
                        SuggestionChip(
                            onClick = { onQuerySelect(sample) },
                            label = { Text(sample, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// 15. Crystal Ball Card
@Composable
fun CrystalBallCard(
    card: AssistantCardData.CrystalBall,
    onAskAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MAGIC 8-BALL",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = AssistantBlue
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "🎱", fontSize = 52.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "“${card.answer}”",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(onClick = onAskAgain, shape = RoundedCornerShape(16.dp)) {
                Text("Ask Another Question")
            }
        }
    }
}

// 16. Animal Sound Card
@Composable
fun AnimalSoundCard(
    card: AssistantCardData.AnimalSound,
    onAnimalSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = card.emoji, fontSize = 56.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The ${card.animal}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "“${card.soundText}”",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = AssistantBlue)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AssistChip(onClick = { onAnimalSelect("what does a lion say") }, label = { Text("🦁 Lion") })
                AssistChip(onClick = { onAnimalSelect("what does a cat say") }, label = { Text("🐱 Cat") })
                AssistChip(onClick = { onAnimalSelect("what does a dog say") }, label = { Text("🐶 Dog") })
            }
        }
    }
}

// 17. Default Assistant Setting Card
@Composable
fun DefaultAssistantSettingCard(
    card: AssistantCardData.DefaultAssistantSetting,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AssistantBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SettingsVoice,
                        contentDescription = "Default Assistant",
                        tint = AssistantBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Default Digital Assistant",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Android System Settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = card.instructions,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Step guide
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Quick Setup Steps:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "1. Tap \"Open Assistant Settings\" below",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "2. Select \"Default digital assistant app\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "3. Choose \"Assistant\" from the list",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = {
                    try {
                        val intent = Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            val intent = Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e2: Exception) {
                            val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AssistantBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Launch,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Assistant Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
}

