package com.techullurgy.pocketcarrom.presentation.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.techullurgy.pocketcarrom.domain.CarromBoard
import com.techullurgy.pocketcarrom.domain.Coin
import com.techullurgy.pocketcarrom.domain.Striker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    fun onEvent(event: GameEvent) {
        when(event) {
            is GameEvent.OnPocketed -> {
                _gameState.update {
                    it.copy(
                        board = it.board.copy(
                            coins = it.board.coins.filterNot { c -> c == event.coin }
                        )
                    )
                }
            }

            is GameEvent.OnStrike -> {
                val currentVelocityAngle = gameState.value.velocityDirectionAngle
                gameState.value.board.striker.strike(currentVelocityAngle, event.magnitude)
                _gameState.update {
                    it.copy(
                        isMyTurn = false,
                        velocityDirectionAngle = 0.0f
                    )
                }
            }
            is GameEvent.OnStrikerMove -> {
                _gameState.update {
                    it.copy(
                        strikerXPercentage = event.percent
                    )
                }
            }
            is GameEvent.OnVelocityDirectionMove -> {
                _gameState.update {
                    it.copy(
                        velocityDirectionAngle = event.percent
                    )
                }
            }
            is GameEvent.OnVelocityMagnitudeSelection -> TODO()

            GameEvent.GameTestEvent.ResetStriker -> {
                _gameState.update {
                    it.copy(
                        velocityDirectionAngle = 0.0f,
                        strikerXPercentage = 0.0f,
                        isMyTurn = true
                    )
                }
            }
        }
    }
}

sealed interface GameEvent {
    data class OnPocketed(val coin: Coin): GameEvent
    data class OnStrikerMove(val percent: Float): GameEvent
    data class OnVelocityDirectionMove(val percent: Float): GameEvent
    data class OnVelocityMagnitudeSelection(val percent: Float): GameEvent
    data class OnStrike(val magnitude: Float): GameEvent

    sealed interface GameTestEvent: GameEvent {
        data object ResetStriker: GameTestEvent
    }
}

data class GameState(
    val playerName: String = "Irsath",
    val playerDegree: Float = 0f,
    val isMyTurn: Boolean = true,
    val pocketedCoins: List<Coin> = emptyList(),
    val board: CarromBoard = CarromBoard(
        coins = List(9) { Coin(it, Color.White) } + List(9) { Coin(it, Color.Black) } + Coin(1, Color.Red),
        striker = Striker()
    ),
    val strikerXPercentage: Float = 0f,
    val velocityDirectionAngle: Float = 0f,
)