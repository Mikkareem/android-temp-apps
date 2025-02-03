package com.techullurgy.pocketcarrom.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StrikerController(
    strikerXPercentage: Float,
    velocityDirectionAngle: Float,
    onStrikerMove: (Float) -> Unit,
    onVelocityDirectionMove: (Float) -> Unit,
    onStrike: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
//        Slider(
//            value = strikerXPercentage,
//            onValueChange = onStrikerMove,
//            modifier = Modifier.fillMaxWidth(.7f)
//        )
        Selector(
            value = strikerXPercentage,
            onValueChange = onStrikerMove,
            modifier = Modifier.fillMaxWidth(.7f)
        )
        Selector(
            value = velocityDirectionAngle,
            onValueChange = onVelocityDirectionMove,
            modifier = Modifier.fillMaxWidth(.7f)
        )
        StrikeBox(
            onStrike = onStrike
        )
    }
}