package com.techullurgy.opengl.gameengine

import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.cross
import dev.romainguy.kotlin.math.dot
import dev.romainguy.kotlin.math.normalize
import dev.romainguy.kotlin.math.perspective

class Camera private constructor(
    surfaceWidth: Int,
    surfaceHeight: Int
) {
    private val ratio = surfaceWidth.toFloat()/surfaceHeight.toFloat()

    private val projectionMatrix = perspective(40f, ratio, .1f, 100f)
    private lateinit var viewMatrix: Mat4

    private val defaultEye = Float3(-5f, -5f, 5f)

    private var eye = defaultEye.copy()

    private var target = Float3(0f, 0f, 0f)
    private val up = Float3(0f, 1f, 0f)

    init {
        setLookTarget(eye, target, up)
    }

    fun rotateCameraX(sign: Float) {
        eye = eye.rotateX(sign * 10f)
        onEyeChanged()
    }

    fun rotateCameraY(sign: Float) {
        eye = eye.rotateY(sign * 10f)
        onEyeChanged()
    }

    fun rotateCameraZ(sign: Float) {
        eye = eye.rotateZ(sign * 10f)
        onEyeChanged()
    }

    fun resetCameraEye() {
        eye = defaultEye.copy()
        onEyeChanged()
    }

    fun getCameraMatrix(): Mat4 {
        return projectionMatrix * viewMatrix
    }

    private fun onEyeChanged() {
        setLookTarget(eye, target, up)
    }

    private fun setLookTarget(position: Float3, target: Float3, up: Float3) {
        setLookDirection(position, target-position, up)
    }

    private fun setLookDirection(position: Float3, direction: Float3, up: Float3) {
        val w = normalize(direction)
        val u = normalize(cross(w, up))
        val v = cross(w, u)

        viewMatrix = Mat4.identity()
        viewMatrix[0][0] = u.x
        viewMatrix[1][0] = u.y
        viewMatrix[2][0] = u.z
        viewMatrix[0][1] = v.x
        viewMatrix[1][1] = v.y
        viewMatrix[2][1] = v.z
        viewMatrix[0][2] = w.x
        viewMatrix[1][2] = w.y
        viewMatrix[2][2] = w.z
        viewMatrix[3][0] = -dot(u, position)
        viewMatrix[3][1] = -dot(v, position)
        viewMatrix[3][2] = -dot(w, position)
    }

    companion object {
        @Volatile
        private var INSTANCE: Camera? = null

        fun getInstance(width: Int, height: Int): Camera {
            if(INSTANCE == null) {
                synchronized(this) {
                    if(INSTANCE == null) {
                        INSTANCE = Camera(width, height)
                    }
                }
            }

            return INSTANCE!!
        }

        fun getInstance() = INSTANCE!!
    }
}