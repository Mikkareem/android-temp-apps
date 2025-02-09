package com.techullurgy.openglsample

data class Move(val x: Int, val y: Int, val z: Int, val dir: Int) {
    var angle = 0f

    fun update(angle: Float) {
        this.angle = angle
    }
}
