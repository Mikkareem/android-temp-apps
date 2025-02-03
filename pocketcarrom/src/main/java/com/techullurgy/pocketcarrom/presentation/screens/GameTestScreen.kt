package com.techullurgy.pocketcarrom.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techullurgy.pocketcarrom.presentation.components.Board
import com.techullurgy.pocketcarrom.presentation.components.StrikerController
import com.techullurgy.pocketcarrom.presentation.viewmodels.GameEvent
import com.techullurgy.pocketcarrom.presentation.viewmodels.GameViewModel

@Composable
fun GameTestScreen(
    viewModel: GameViewModel,
) {
    val density = LocalDensity.current

    val state by viewModel.gameState.collectAsStateWithLifecycle()

    val board = state.board

    LaunchedEffect(Unit) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                board.collisionResolution(density)
                board.update(density)?.let { pocketedCoin ->
                    viewModel.onEvent(GameEvent.OnPocketed(pocketedCoin))
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Board(
                angle = 0f,
                board = board,
                strikerXPercentage = state.strikerXPercentage,
                isOnStrikerMove = state.isMyTurn,
                velocityDirectionAngle = state.velocityDirectionAngle
            )

            AnimatedContent (
                targetState = state.isMyTurn
            ) { isMyTurn ->
                if(isMyTurn) {
                    StrikerController(
                        strikerXPercentage = state.strikerXPercentage,
                        velocityDirectionAngle = state.velocityDirectionAngle,
                        onStrikerMove = { viewModel.onEvent(GameEvent.OnStrikerMove(it)) },
                        onVelocityDirectionMove = { viewModel.onEvent(GameEvent.OnVelocityDirectionMove(it)) },
                        onStrike = { viewModel.onEvent(GameEvent.OnStrike(it)) }
                    )
                } else {
                    Button(
                        onClick = { viewModel.onEvent(GameEvent.GameTestEvent.ResetStriker) }
                    ) {
                        Text("Reset Striker")
                    }
                }
            }
        }
    }
}