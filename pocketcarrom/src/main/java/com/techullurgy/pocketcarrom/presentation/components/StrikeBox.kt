package com.techullurgy.pocketcarrom.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

@Composable
fun StrikeBox(
    onStrike: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val strikerVelocityMagnitudeAnimatable = remember { Animatable(0f) }

    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(Unit) {
        snapshotFlow { isPressed }
            .drop(1) // Drop initial value on composition
            .collectLatest {
                if(it) {
                    strikerVelocityMagnitudeAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                } else {
                    val currentValue = strikerVelocityMagnitudeAnimatable.value
                    onStrike(lerp(10f, 50f, currentValue))
                    strikerVelocityMagnitudeAnimatable.snapTo(0f)
                }
            }
    }

    Box(
        modifier = modifier
            .size(60.dp)
            .drawWithCache {
                onDrawBehind {
                    drawCircle(
                        color = Color.Magenta,
                        radius = lerp(0.5f, 1f, strikerVelocityMagnitudeAnimatable.value) * size.width
                    )
                    drawCircle(Color.Yellow)
                }
            }
            .clickable(onClick = {}, interactionSource = interactionSource, indication = null)
    )
}