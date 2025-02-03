package com.techullurgy.openglsample

import android.content.Context
import android.opengl.GLSurfaceView

class OpenGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer = RubiksCubeRenderer()

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun resetCameraPosition() {
        renderer.resetCameraPosition()
        requestRender()
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

    fun xTurn(index: Int, dir: Int) {
        renderer.turnX(index, dir)
        requestRender()
    }

    fun yTurn(index: Int, dir: Int) {
        renderer.turnY(index, dir)
        requestRender()
    }

    fun zTurn(index: Int, dir: Int) {
        renderer.turnZ(index, dir)
        requestRender()
    }
}