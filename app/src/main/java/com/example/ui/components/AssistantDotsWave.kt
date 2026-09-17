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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AssistantBlue
import com.example.ui.theme.AssistantGreen
import com.example.ui.theme.AssistantRed
import com.example.ui.theme.AssistantYellow
import kotlin.math.sin

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
    dotSize: Dp = 10.dp,
    spacing: Dp = 6.dp,
    modifier: Modifier = Modifier
) {
    val colors = listOf(AssistantBlue, AssistantRed, AssistantYellow, AssistantGreen)

    val infiniteTransition = rememberInfiniteTransition(label = "AssistantDotsTransition")

    // Continuous 0..1 wave progress
    val waveProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    AssistantDotState.LISTENING -> 800
                    AssistantDotState.THINKING -> 600
                    AssistantDotState.SPEAKING -> 700
                    AssistantDotState.IDLE -> 2000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_progress"
    )

    // Clamped RMS to prevent wild expansions
    val clampedRms = audioRms.coerceIn(0f, 1f)

    Row(
        modifier = modifier.height(dotSize * 2.2f),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEachIndexed { index, color ->
            val phase = waveProgress + (index * 0.75f)
            val sineVal = sin(phase.toDouble()).toFloat()

            val yOffsetFraction = when (state) {
                AssistantDotState.IDLE -> 0f
                AssistantDotState.LISTENING -> sineVal * (0.35f + (clampedRms * 0.35f))
                AssistantDotState.THINKING -> sineVal * 0.45f
                AssistantDotState.SPEAKING -> sineVal * 0.3f
            }

            val scaleMultiplier = when (state) {
                AssistantDotState.IDLE -> 1f
                AssistantDotState.LISTENING -> 1f + (sineVal.coerceAtLeast(0f) * 0.25f) + (clampedRms * 0.2f)
                AssistantDotState.THINKING -> 1f + (sineVal.coerceAtLeast(0f) * 0.2f)
                AssistantDotState.SPEAKING -> 1f + (sineVal.coerceAtLeast(0f) * 0.15f)
            }

            val maxOffsetDp = (dotSize.value * yOffsetFraction).dp
            val actualSize = (dotSize.value * scaleMultiplier).coerceIn(dotSize.value * 0.8f, dotSize.value * 1.5f).dp

            Box(
                modifier = Modifier
                    .offset(y = maxOffsetDp)
                    .size(actualSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

