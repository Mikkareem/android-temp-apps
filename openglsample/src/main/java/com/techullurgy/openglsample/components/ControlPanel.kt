package com.techullurgy.openglsample.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techullurgy.openglsample.Move
import com.techullurgy.openglsample.OpenGLSurfaceView
import kotlinx.coroutines.launch

private const val MoveTime = 500

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ControlPanel(
    view: OpenGLSurfaceView?,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val moveAngle = remember { Animatable(0f) }

        val scope = rememberCoroutineScope()

        ProvideTextStyle(LocalTextStyle.current.copy(color = Color.White)) {
            Control("F", {
                val currentMove = Move(-1, 0, 0, -1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("F'", {
                val currentMove = Move(-1, 0, 0, 1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("U", {
                val currentMove = Move(0, -1, 0, -1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("U'", {
                val currentMove = Move(0, -1, 0, 1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("L", {
                val currentMove = Move(0, 0, -1, 1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("L'", {
                val currentMove = Move(0, 0, -1, -1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("B", {
                val currentMove = Move(1, 0, 0, -1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("B'", {
                val currentMove = Move(1, 0, 0, 1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("D", {
                val currentMove = Move(0, 1, 0, -1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("D'", {
                val currentMove = Move(0, 1, 0, 1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("R", {
                val currentMove = Move(0, 0, 1, 1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })

            Control("R'", {
                val currentMove = Move(0, 0, 1, -1)
                scope.launch {
                    try {
                        moveAngle.snapTo(0f)
                        moveAngle.animateTo(90f, tween(MoveTime)) {
                            view?.animateTurn(currentMove, value)
                        }
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        view?.turn(currentMove)
                    }
                }
            })
        }
    }
}

@Composable
private fun Control(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.Magenta)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(name, fontSize = 24.sp)
    }
}