package com.techullurgy.openglsample

import android.content.Context
import android.opengl.GLSurfaceView
import kotlin.math.abs

class OpenGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer = RubiksCubeRenderer()

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun rotateCameraX(angle: Float) {
        renderer.rotateCameraX(angle)
        requestRender()
    }

    fun rotateCameraY(angle: Float) {
        renderer.rotateCameraY(angle)
        requestRender()
    }

    fun rotateCameraZ(angle: Float) {
        renderer.rotateCameraZ(angle)
        requestRender()
    }

    fun turn(move: Move) {
        if(abs(move.x) > 0) {
            renderer.turnX(move.x, move.dir)
        } else if(abs(move.y) > 0) {
            renderer.turnY(move.y, move.dir)
        } else if(abs(move.z) > 0) {
            renderer.turnZ(move.z, move.dir)
        }
        requestRender()
    }

    fun animateTurn(move: Move, angle: Float) {
        renderer.updateCurrentMove(move, angle)
        requestRender()
    }
}