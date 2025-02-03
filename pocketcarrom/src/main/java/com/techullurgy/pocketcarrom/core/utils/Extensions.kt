package com.techullurgy.pocketcarrom.core.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun Offset.getRotatedOffset(angle: Float, pivot: Offset): Offset {
    val angleInRadians = Math.toRadians(angle.toDouble())
    val c = cos(angleInRadians).toFloat()
    val s = sin(angleInRadians).toFloat()

    val newPosition = this - pivot

    val rotatedOffset = Offset(
        x = newPosition.x*c - newPosition.y*s,
        y = newPosition.x*s + newPosition.y*c
    )

    return rotatedOffset + pivot
}

fun Offset.dot(other: Offset): Float {
    return x * other.x + y * other.y
}

fun Offset.setMag(newMag: Float): Offset {
    val currentMag = mag()
    val magnitudeRatio = newMag / currentMag
    return Offset(
        x = x * magnitudeRatio,
        y = y * magnitudeRatio
    )
}

fun Offset.mag(): Float = sqrt(x * x + y * y)

fun Offset.normalize() = this / this.mag()