package com.techullurgy.openglsample

import android.opengl.GLES32
import android.opengl.GLSurfaceView.Renderer
import android.os.Build
import androidx.annotation.RequiresApi
import com.techullurgy.opengl.gameengine.Camera
import com.techullurgy.opengl.gameengine.Transformation
import com.techullurgy.opengl.gameengine.rotate2D
import com.techullurgy.openglsample.shapes.test.Cube
import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.properties.Delegates

class RubiksCubeRenderer: Renderer {
    private val cubes = mutableListOf<Cube>()

    private var width by Delegates.notNull<Int>()
    private var height by Delegates.notNull<Int>()

    private lateinit var camera: Camera

    private var isStarted = false

    private var worldRotationX = 0f
    private var worldRotationY = 0f
    private var worldRotationZ = 0f

    fun rotateCameraX(angle: Float) {
        if(isStarted) {
            worldRotationX = angle
        }
    }

    fun rotateCameraY(angle: Float) {
        if(isStarted) {
            worldRotationY = angle
        }
    }

    fun rotateCameraZ(angle: Float) {
        if(isStarted) {
            worldRotationZ = angle
        }
    }

    fun resetCameraPosition() {
        if(isStarted) {
            worldRotationX = 0f
            worldRotationY = 0f
            worldRotationZ = 0f
        }
    }

    private fun render() {
        Transformation.push()
        Transformation.rotateX(worldRotationX)
        Transformation.rotateY(worldRotationY)
        Transformation.rotateZ(worldRotationZ)
        cubes
            .forEach { cube ->
                Transformation.push()
                cube.draw()
                Transformation.pop()
            }
        Transformation.pop()
    }

    fun turnX(index: Int, dir: Int) {
        cubes
            .filter { it.x == index }
            .forEach { cube ->
                val new2DPosition = rotate2D(dir*90f, Float2(cube.y.toFloat(), cube.z.toFloat()))
                cube.update(Float3(cube.x.toFloat(), new2DPosition.x, new2DPosition.y))
                cube.turnFacesX(dir)
            }
    }

    fun turnY(index: Int, dir: Int) {
        cubes
            .filter { it.y == index }
            .forEach { cube ->
                val new2DPosition = rotate2D(dir*90f, Float2(cube.x.toFloat(), cube.z.toFloat()))
                cube.update(Float3(new2DPosition.x, cube.y.toFloat(), new2DPosition.y))
                cube.turnFacesY(dir)
            }
    }

    fun turnZ(index: Int, dir: Int) {
        cubes
            .filter { it.z == index }
            .forEach { cube ->
                val new2DPosition = rotate2D(dir*90f, Float2(cube.x.toFloat(), cube.y.toFloat()))
                cube.update(Float3(new2DPosition.x, new2DPosition.y, cube.z.toFloat()))
                cube.turnFacesZ(dir)
            }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        isStarted = true

        GLES32.glClearColor(0f, 0f, 0f, 1f)

        (-1..1).forEach { x ->
            (-1..1).forEach { y ->
                (-1..1).forEach { z ->
                    cubes.add(Cube(Float3(x.toFloat(), y.toFloat(), z.toFloat())))
                }
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        this.width = width
        this.height = height

        camera = Camera.getInstance(width, height)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        render()
        Transformation.clearAll()
    }
}