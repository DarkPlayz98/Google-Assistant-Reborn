package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AssistantBlue
import com.example.ui.theme.AssistantGreen
import com.example.ui.theme.AssistantRed
import com.example.ui.theme.AssistantYellow

enum class AssistantDotState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

@Composable
fun AssistantDotsWave(
    state: AssistantDotState,
    audioRms: Float = 0f,
    dotSize: Dp = 12.dp,
    spacing: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    val colors = listOf(AssistantBlue, AssistantRed, AssistantYellow, AssistantGreen)

    // Infinite transition for animated states
    val infiniteTransition = rememberInfiniteTransition(label = "AssistantDotsTransition")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEachIndexed { index, color ->
            val phaseDelay = index * 140

            val yOffset by when (state) {
                AssistantDotState.IDLE -> {
                    // Subtle breathing idle animation
                    infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "idle_$index"
                    )
                }
                AssistantDotState.LISTENING -> {
                    // Responsive wave responding to voice RMS amplitude + periodic sine wave
                    val targetWave = (14f + (audioRms * 18f))
                    infiniteTransition.animateFloat(
                        initialValue = -targetWave,
                        targetValue = targetWave,
                        animationSpec = infiniteRepeatable(
                            animation = tween(380, delayMillis = phaseDelay, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "listening_$index"
                    )
                }
                AssistantDotState.THINKING -> {
                    // Orbiting / jumping thinking dot wave
                    infiniteTransition.animateFloat(
                        initialValue = -12f,
                        targetValue = 12f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(420, delayMillis = phaseDelay, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "thinking_$index"
                    )
                }
                AssistantDotState.SPEAKING -> {
                    // Smooth rhythmic speaking bounce
                    infiniteTransition.animateFloat(
                        initialValue = -8f,
                        targetValue = 8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(320, delayMillis = phaseDelay, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "speaking_$index"
                    )
                }
            }

            val scaleMultiplier by when (state) {
                AssistantDotState.LISTENING -> {
                    infiniteTransition.animateFloat(
                        initialValue = 0.9f,
                        targetValue = 1.3f + (audioRms * 0.4f),
                        animationSpec = infiniteRepeatable(
                            animation = tween(380, delayMillis = phaseDelay, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale_listening_$index"
                    )
                }
                AssistantDotState.SPEAKING -> {
                    infiniteTransition.animateFloat(
                        initialValue = 0.95f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(320, delayMillis = phaseDelay, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale_speaking_$index"
                    )
                }
                else -> {
                    rememberInfiniteTransition(label = "scale_idle").animateFloat(
                        initialValue = 1f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(tween(1000)),
                        label = "scale_idle_$index"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = yOffset.dp)
                    .size(dotSize * scaleMultiplier)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
