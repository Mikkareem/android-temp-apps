package com.techullurgy.openglsample.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.techullurgy.openglsample.OpenGLSurfaceView
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private val GearColor = Color.White

@Composable
fun CameraGears(
    view: OpenGLSurfaceView?,
    modifier: Modifier = Modifier
) {

    var xAngle by remember { mutableFloatStateOf(0f) }
    var yAngle by remember { mutableFloatStateOf(0f) }
    var zAngle by remember { mutableFloatStateOf(0f) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Gear(
            degree = xAngle,
            onDegreeChange = {
                xAngle = it
                view?.rotateCameraX(it)
            },
        )
        Gear(
            degree = yAngle,
            onDegreeChange = {
                yAngle = it
                view?.rotateCameraY(it)
            },
        )
        Gear(
            degree = zAngle,
            onDegreeChange = {
                zAngle = it
                view?.rotateCameraZ(it)
            },
        )
    }
}

@Composable
private fun Gear(
    degree: Float,
    onDegreeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onDegreeChange(0f) },
                    onDragCancel = { onDegreeChange(0f) }
                ) { change, _ ->
                    val translatedPos = change.position - size.center.toOffset()
                    val theta = atan2(translatedPos.y, translatedPos.x) * (180f / (22f/7f))
                    onDegreeChange(theta)
                }
            }
            .drawBehind {
                drawCircle(
                    color = Color.Red,
                    style = Stroke(10.dp.toPx())
                )

                if(degree != 0f) {
                    val unitX = cos(Math.toRadians(degree.toDouble())).toFloat()
                    val unitY = sin(Math.toRadians(degree.toDouble())).toFloat()

                    val scaleFactor = 150f

                    val pos = Offset(unitX * scaleFactor, unitY * scaleFactor)

                    val path = Path().apply {
                        moveTo(center.x, center.y)
                        relativeLineTo(pos.x, pos.y)
                    }

                    drawPath(
                        path = path,
                        style = Stroke(7.dp.toPx()),
                        color = GearColor
                    )

                    drawCircle(
                        color = GearColor,
                        center = center+pos,
                        radius = 15.dp.toPx()
                    )
                } else {
                    drawCircle(
                        color = GearColor,
                        radius = 15.dp.toPx()
                    )
                }
            }
    )
}