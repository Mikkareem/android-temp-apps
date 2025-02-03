package com.techullurgy.pocketcarrom.presentation.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.techullurgy.pocketcarrom.BoardSizeInDp
import com.techullurgy.pocketcarrom.MaxVelocityDirectionAngle

internal fun DrawScope.drawBoard(
    velocityDirection: Float,
    strikerPosition: Offset
) {
    val generalPadding = size.width * 0.006f
    // Center
    drawCircle(
        color = Color.Black,
        style = Stroke(generalPadding),
        radius = size.width * 0.15f
    )
    drawCircle(
        color = Color.Magenta,
        style = Stroke(generalPadding),
        radius = size.width * 0.025f
    )

    val holeRadius = size.width * .055f

    // Minuses
    val minusPath = Path().apply {
        moveTo(2 * holeRadius + generalPadding, 2 * holeRadius + generalPadding)
        relativeLineTo(size.width/6f, size.height/6f)
        addOval(
            oval = Rect(
                center = Offset(
                    2 * holeRadius + generalPadding + size.width/(BoardSizeInDp.toFloat()/15f),
                    2 * holeRadius + generalPadding + size.height/(BoardSizeInDp.toFloat()/15f)
                ),
                radius = size.width/60f
            )
        )
        addArc(
            oval = Rect(
                center = Offset(
                    2 * holeRadius + generalPadding + size.width/(BoardSizeInDp.toFloat()/40f),
                    2 * holeRadius + generalPadding + size.height/(BoardSizeInDp.toFloat()/40f)
                ),
                radius = holeRadius * 0.8f
            ),
            startAngleDegrees = 250f,
            sweepAngleDegrees = 300f
        )
    }

    val eachAngle = 360f / 4
    repeat(4) {
        rotate(eachAngle * it) {
            drawPath(
                path = minusPath,
                color = Color.Black,
                style = Stroke(2.dp.toPx())
            )
            // StrikeArea
            drawLine(
                color = Color.Black,
                start = Offset(
                    size.width * .2f,
                    2 * holeRadius
                ),
                end = Offset(
                    size.width - (size.width * .2f),
                    2 * holeRadius
                ),
                strokeWidth = 4.dp.toPx()
            )

            drawLine(
                color = Color.Black,
                start = Offset(
                    size.width * .2f,
                    2 * holeRadius + size.width/(BoardSizeInDp.toFloat()/15f)
                ),
                end = Offset(
                    size.width - (size.width * .2f),
                    2 * holeRadius + size.width/(BoardSizeInDp.toFloat()/15f)
                ),
                strokeWidth = 2.dp.toPx()
            )
        }
    }

    repeat(4) {
        rotate(eachAngle * it) {
            drawCircle(
                color = Color.Red,
                style = Stroke(4.dp.toPx()),
                center = Offset(
                    size.width * 0.2f,
                    size.height * 0.13f
                ),
                radius = .5f * size.width/(BoardSizeInDp.toFloat()/15f)
            )
            drawCircle(
                color = Color.Red,
                style = Stroke(4.dp.toPx()),
                center = Offset(
                    size.width * 0.13f,
                    size.height * 0.2f
                ),
                radius = .5f * size.width/(BoardSizeInDp.toFloat()/15f)
            )
        }
    }

    if(velocityDirection != 0f) {
        val angle = lerp(-MaxVelocityDirectionAngle, MaxVelocityDirectionAngle, velocityDirection)

        translate(
            strikerPosition.x,
            strikerPosition.y
        ) {
            rotate(
                degrees = angle,
                pivot = Offset.Zero
            ) {
                val length = size.height * 0.65f
                drawLine(
                    color = Color.Black,
                    start = Offset.Zero,
                    end = Offset(0f, -length),
                    strokeWidth = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f))
                )
            }
        }
    }
}