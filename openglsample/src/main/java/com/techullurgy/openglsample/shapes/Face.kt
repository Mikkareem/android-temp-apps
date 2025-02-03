package com.techullurgy.openglsample.shapes

import androidx.compose.ui.graphics.Color
import com.techullurgy.openglsample.shapes.test.Square
import dev.romainguy.kotlin.math.Float3

data class Face(
    val normal: Float3,
    val color: Color
) {
    private val square: Square = Square(color)

    fun turnX(angle: Float) {}

    fun turnY(angle: Float) {}

    fun turnZ(angle: Float) {}

    fun show() {

    }
}
