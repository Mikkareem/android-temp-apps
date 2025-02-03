package com.techullurgy.openglsample.shapes.test

import androidx.compose.ui.graphics.Color
import com.techullurgy.opengl.gameengine.Transformation
import dev.romainguy.kotlin.math.Float3
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

data class Face(
    private var normal: Float3,
    private val color: Color
) {
    fun show() {
        Transformation.push()
        Transformation.translate(normal * 0.5f)
        if(abs(normal.x) > 0f) {
            Transformation.rotateY(normal.x * 90f)
        } else if(abs(normal.y) > 0f) {
            Transformation.rotateX(normal.y * 90f)
        }
        Square(color).draw()
        Transformation.pop()
    }

    fun turnX(angle: Float) {
        val temp = Float3()
        temp.x = round(normal.x)
        temp.y = round(normal.y * cos(angle) - normal.z * sin(angle))
        temp.z = round(normal.y * sin(angle) + normal.z * cos(angle))
        normal = temp
    }

    fun turnY(angle: Float) {
        val temp = Float3()
        temp.x = round(normal.x * cos(angle) - normal.z * sin(angle))
        temp.y = round(normal.y)
        temp.z = round(normal.x * sin(angle) + normal.z * cos(angle))
        normal = temp
    }

    fun turnZ(angle: Float) {
        val temp = Float3()
        temp.x = round(normal.x * cos(angle) - normal.y * sin(angle))
        temp.y = round(normal.x * sin(angle) + normal.y * cos(angle))
        temp.z = round(normal.z)
        normal = temp
    }
}