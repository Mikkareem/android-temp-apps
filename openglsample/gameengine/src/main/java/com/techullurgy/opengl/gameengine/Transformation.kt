package com.techullurgy.opengl.gameengine

import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Mat3
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.Quaternion
import dev.romainguy.kotlin.math.radians
import dev.romainguy.kotlin.math.translation
import java.util.Stack
import kotlin.math.cos
import kotlin.math.sin

object Transformation {
    private val transformations: Stack<Mat4> = Stack()

    fun push() {
        if(transformations.empty()) {
            transformations.push(Mat4.identity())
        } else {
            transformations.push(transformations.peek())
        }
    }

    fun pop() {
        transformations.pop()
    }

    fun clearAll() {
        while(transformations.isNotEmpty()) {
            transformations.pop()
        }
    }

    fun calculate(): Mat4 {
        return if(transformations.empty()) Mat4.identity() else transformations.peek()
    }

    fun applyMatrix(matrix: Mat4) {
        if(transformations.empty()) {
            transformations.push(Mat4.identity())
        }

        val currentMatrix = transformations.peek()
        transformations.pop()

        val appliedMatrix = currentMatrix * matrix
        transformations.push(appliedMatrix)
    }

    fun translate(position: Float3) {
        if(transformations.empty()) {
            transformations.push(Mat4.identity())
        }

        val currentMatrix = transformations.peek()
        transformations.pop()

        val translatedMatrix = currentMatrix * translation(position)
        transformations.push(translatedMatrix)
    }

    fun rotateX(angle: Float, position: Float3 = Float3()) {
        if(transformations.empty()) {
            transformations.push(Mat4.identity())
        }

        val currentMatrix = transformations.peek()
        transformations.pop()

        val axis = Float3(1f, 0f, 0f)
        val quat = Quaternion.fromAxisAngle(axis, angle)
        val rotatedPosition = position.rotateX(angle)
        val rotatedMatrix = currentMatrix * translation(rotatedPosition) * quat.toMatrix()
        transformations.push(rotatedMatrix)
    }

    fun rotateY(angle: Float, position: Float3 = Float3()) {
        if(transformations.empty()) {
            transformations.push(Mat4.identity())
        }

        val currentMatrix = transformations.peek()
        transformations.pop()

        val axis = Float3(0f, 1f, 0f)
        val quat = Quaternion.fromAxisAngle(axis, angle)
        val rotatedPosition = position.rotateY(angle)
        val rotatedMatrix = currentMatrix * translation(rotatedPosition) * quat.toMatrix()
        transformations.push(rotatedMatrix)
    }

    fun rotateZ(angle: Float, position: Float3 = Float3()) {
        if(transformations.empty()) {
            transformations.push(Mat4.identity())
        }

        val currentMatrix = transformations.peek()
        transformations.pop()

        val axis = Float3(0f, 0f, 1f)
        val quat = Quaternion.fromAxisAngle(axis, angle)
        val rotatedPosition = position.rotateZ(angle)
        val rotatedMatrix = currentMatrix * translation(rotatedPosition) * quat.toMatrix()
        transformations.push(rotatedMatrix)
    }
}

fun Float3.rotateX(angle: Float): Float3 {
    val c = cos(radians(angle))
    val s = sin(radians(angle))

    return Mat3.of(
        1f, 0f, 0f,
        0f, c, -s,
        0f, s, c
    ) * this
}

fun Float3.rotateY(angle: Float): Float3 {
    val c = cos(radians(angle))
    val s = sin(radians(angle))

    return Mat3.of(
        c, 0f, s,
        0f, 1f, 0f,
        -s, 0f, c
    ) * this
}

fun Float3.rotateZ(angle: Float): Float3 {
    val c = cos(radians(angle))
    val s = sin(radians(angle))

    return Mat3.of(
        c, -s, 0f,
        s, c, 0f,
        0f, 0f, 1f
    ) * this
}

fun rotate2D(angle: Float, position: Float2): Float2 {
    val output = Float3(position, 0f).rotateZ(angle).xy
    return output
}