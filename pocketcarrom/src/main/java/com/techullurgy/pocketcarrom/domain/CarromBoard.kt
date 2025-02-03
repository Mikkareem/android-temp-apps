package com.techullurgy.pocketcarrom.domain

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Density

@Immutable
data class CarromBoard(
    val coins: List<Coin>,
    val striker: Striker,
    val holes: List<Hole> = List(4) { Hole(it) }
) {
    fun update(density: Density): Coin? {
        coins.forEach { it.update(density) }
        striker.update(density)

        coins.forEach { c ->
            holes.forEach { h ->
                if(h.isPocketed(c)) {
                    return c
                }
            }
        }
        holes.forEach {
            if(it.isPocketed(striker)) {
                return striker
            }
        }

        return null
    }

    fun collisionResolution(density: Density) {
        for(i in 0 until coins.size-1) {
            val currentLeft = coins[i]
            for(j in i+1 until coins.size) {
                val currentRight = coins[j]
                currentLeft.collide(currentRight, density)
            }
            striker.collide(currentLeft, density)
        }
        striker.collide(coins.last(), density)
    }
}
