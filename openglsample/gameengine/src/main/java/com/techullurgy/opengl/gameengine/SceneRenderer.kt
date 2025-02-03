package com.techullurgy.opengl.gameengine

import java.nio.ShortBuffer

object SceneRenderer {
    fun drawTriangleStrips(indices: ShortBuffer, size: Int) {
        GL.glDrawElements(GL.GL_TRIANGLE_STRIP, size, GL.GL_UNSIGNED_SHORT, indices)
    }
}