package com.techullurgy.pocketcarrom.domain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced

private const val HexHoleColor = 0xff000000

@Stable
class Hole(
    private val id: Int
) {

    private var boardCoordinates: LayoutCoordinates? = null
    private var holeCoordinates: LayoutCoordinates? = null

    fun isPocketed(coin: Coin): Boolean {
        return boardCoordinates?.let {
            val coinBoundingBox = boardCoordinates!!.localBoundingBoxOf(coin.coinCoordinates!!)
            val holeBoundingBox = boardCoordinates!!.localBoundingBoxOf(holeCoordinates!!)

            holeBoundingBox.toAndroidRectF().contains(coinBoundingBox.toAndroidRectF())
        } ?: false
    }

    @Composable
    fun Content(
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .onPlaced {
                    holeCoordinates = it
                    boardCoordinates = it.parentLayoutCoordinates
                }
                .clip(CircleShape)
                .background(Color(HexHoleColor))
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hole) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}