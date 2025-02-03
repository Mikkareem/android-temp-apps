package com.techullurgy.pocketcarrom.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.techullurgy.pocketcarrom.MaxVelocityDirectionAngle
import com.techullurgy.pocketcarrom.StrikerSizeInDp
import com.techullurgy.pocketcarrom.core.utils.normalize
import com.techullurgy.pocketcarrom.core.utils.setMag
import kotlin.math.cos
import kotlin.math.sin

class Striker: Coin(1, Color.Green) {
    override val coinSize: Dp
        get() = StrikerSizeInDp.dp

    override fun DrawScope.draw() {
        drawCircle(Color.Yellow)

        drawCircle(
            color = Color.Blue,
            radius = (size.width - center.x) * .5f
        )
    }

    fun strike(directionAnglePercent: Float, magnitude: Float) {
        val directionAngle =
            lerp(-MaxVelocityDirectionAngle, MaxVelocityDirectionAngle, directionAnglePercent) - 90
        val angleInRadians = Math.toRadians(directionAngle.toDouble())
        val length = 10f
        val x = length * cos(angleInRadians)
        val y = length * sin(angleInRadians)

        velocity = Offset(
            x = x.toFloat(),
            y = y.toFloat(),
        ).normalize().setMag(magnitude)
    }
}