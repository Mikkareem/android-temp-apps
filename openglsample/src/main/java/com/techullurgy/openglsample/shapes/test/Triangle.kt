package com.techullurgy.openglsample.shapes.test

import android.opengl.GLES32
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Mat4
import dev.romainguy.kotlin.math.scale
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Triangle {
    companion object {
        const val COORDS_PER_VERTEX = 3
        val triangleCoords = floatArrayOf(    // in counterclockwise order:
            0.0f, 0.5f, 0.0f,      // top
            -0.5f, -0.5f, 0.0f,    // bottom left
            0.5f, -0.5f, 0.0f      // bottom right
        )
    }

    private val color = floatArrayOf(
        0.543436f,
        0.290128f,
        0.3892f,
        1f,
    )

    private var program: Int

    private var positionHandle = 0
    private var colorHandle = 0
    private var mvpMatrixHandle = 0

    private var vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private var vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(triangleCoords)
            position(0)
        }
    }

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }        
    """.trimIndent()


    init {
        val vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES32.glCreateProgram().also {
            GLES32.glAttachShader(it, vertexShader)
            GLES32.glAttachShader(it, fragmentShader)
            GLES32.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String) : Int {
        return GLES32.glCreateShader(type).also { shader ->
            GLES32.glShaderSource(shader, shaderCode)
            GLES32.glCompileShader(shader)
        }
    }

    fun draw(mvp: Mat4) {
        GLES32.glUseProgram(program)

        positionHandle = GLES32.glGetAttribLocation(program, "vPosition")

        GLES32.glEnableVertexAttribArray(positionHandle)
        GLES32.glVertexAttribPointer(
            positionHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT,
            false, vertexStride, vertexBuffer
        )

        colorHandle = GLES32.glGetUniformLocation(program, "vColor")
        mvpMatrixHandle = GLES32.glGetUniformLocation(program, "uMVPMatrix")

        GLES32.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvp.toFloatArray(), 0)
        GLES32.glUniform4fv(colorHandle, 1, color, 0)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

        val scaled = scale(Float3(1.17f)) * mvp
        GLES32.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scaled.toFloatArray(), 0)
        GLES32.glUniform4fv(colorHandle, 1, floatArrayOf(1f, 0f, 1f, 1f), 0)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array
        GLES32.glDisableVertexAttribArray(positionHandle)
    }
}