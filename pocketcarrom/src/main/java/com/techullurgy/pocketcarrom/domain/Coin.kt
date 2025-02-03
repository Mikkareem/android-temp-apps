package com.techullurgy.pocketcarrom.domain

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.techullurgy.pocketcarrom.CoinSizeInDp
import com.techullurgy.pocketcarrom.core.utils.dot
import com.techullurgy.pocketcarrom.core.utils.mag
import com.techullurgy.pocketcarrom.core.utils.setMag
import kotlin.math.abs
import kotlin.random.Random

@Stable
open class Coin(
    private val id: Int,
    val color: Color
) {
    var position by mutableStateOf(Offset.Unspecified)
        private set

    private var boardCoordinates: LayoutCoordinates? = null
    var coinCoordinates: LayoutCoordinates? = null
        private set

    protected open val coinSize = CoinSizeInDp.dp

    private val mass = Random.nextDouble(2.0,8.0)

    @Composable
    fun Content() {
        Box(
            Modifier
                .onPlaced {
                    coinCoordinates = it
                    boardCoordinates = it.parentLayoutCoordinates
                }
                .drawBehind {
                    draw()
                }
        )
    }

    protected open fun DrawScope.draw() {
        drawCircle(color)
    }

    protected var velocity = Offset.Zero

    fun updatePosition(pos: Offset) {
        position = pos.copy()
    }

    fun update(density: Density) {
        if(abs(velocity.x) < 0.1f && abs(velocity.y) < 0.1f) {
            velocity = Offset.Zero
        } else {
            edgeDetection(density)
            position += velocity
            velocity *= 0.96f // (Friction / Brake)
        }
    }

    private fun edgeDetection(density: Density) {
        val boardWidth = boardCoordinates!!.size.width
        val boardHeight = boardCoordinates!!.size.height

        val coinWidth = with(density) { coinSize.toPx() }
        val coinHeight = with(density) { coinSize.toPx() }

        if(position.x < coinWidth/2) {
            position = position.copy(x = coinWidth/2)
            velocity = velocity.copy(x = velocity.x * -1f)
        } else if(position.x > boardWidth - coinWidth/2) {
            position = position.copy(x = boardWidth - coinWidth/2)
            velocity = velocity.copy(x = velocity.x * -1f)
        }

        if(position.y < coinHeight/2) {
            position = position.copy(y = coinHeight/2)
            velocity = velocity.copy(y = velocity.y * -1f)
        } else if(position.y > boardHeight - coinHeight/2) {
            position = position.copy(y = boardHeight - coinHeight/2)
            velocity = velocity.copy(y = velocity.y * -1f)
        }
    }

    internal fun collide(other: Coin, density: Density) {
        val otherPosition = other.position
        val currPosition = position

        val impactVector = otherPosition - currPosition
        var distance = impactVector.mag()

        if(distance < with(density) { coinSize.toPx()/2f + other.coinSize.toPx()/2f }) {
            val massSum = mass + other.mass

            val overlap = with(density) { distance - (coinSize.toPx()/2f + other.coinSize.toPx()/2f) }
            var dir = impactVector.copy()
            dir = dir.setMag(overlap * 0.5f)
            other.position -= dir
            this.position += dir

            distance = with(density) { coinSize.toPx()/2f + other.coinSize.toPx()/2f }
            impactVector.setMag(distance)

            val vDiff = other.velocity - this.velocity
            val numerator = vDiff.dot(impactVector)
            val denominator = massSum.toFloat() * distance * distance

            var deltaVA = impactVector.copy()
            deltaVA *= 2 * other.mass.toFloat() * numerator/denominator
            velocity += deltaVA

            var deltaVB = impactVector.copy()
            deltaVB *= -2 * mass.toFloat() * numerator/denominator
            other.velocity += deltaVB
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Coin) return false

        if (id != other.id) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + color.hashCode()
        return result
    }
}
