package com.techullurgy.pocketcarrom.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.lerp
import com.techullurgy.pocketcarrom.BoardSizeInDp
import com.techullurgy.pocketcarrom.CoinSizeInDp
import com.techullurgy.pocketcarrom.HoleSizeInDp
import com.techullurgy.pocketcarrom.StrikerSizeInDp
import com.techullurgy.pocketcarrom.core.utils.getRotatedOffset
import com.techullurgy.pocketcarrom.domain.CarromBoard
import com.techullurgy.pocketcarrom.domain.Coin
import com.techullurgy.pocketcarrom.domain.Hole
import com.techullurgy.pocketcarrom.domain.Striker
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val HexBoardInnerColor = 0xffa27a4d
private const val HexBoardOuterColor = 0xff444444

@Composable
fun Board(
    angle: Float,
    board: CarromBoard,
    modifier: Modifier = Modifier,
    isOnStrikerMove: Boolean = true,
    velocityDirectionAngle: Float = 0f,
    strikerXPercentage: Float = 0f
) {
    val coins: List<Coin> = board.coins
    val striker: Striker = board.striker
    val holes: List<Hole> = board.holes

    val whiteCoins = coins.filter { it.color == Color.White }
    val blackCoins = coins.filter { it.color == Color.Black }
    val redCoin = coins.first { it.color == Color.Red }

    Box(
        modifier = Modifier
            .background(Color(HexBoardOuterColor))
            .padding(16.dp)
    ) {
        Layout(
            modifier = modifier
                .size(BoardSizeInDp.dp)
                .background(Color(HexBoardInnerColor))
                .drawBehind { drawBoard(velocityDirectionAngle, striker.position) },
            contents = listOf(
                @Composable { whiteCoins.forEach { it.Content() } },
                @Composable { blackCoins.forEach { it.Content() } },
                @Composable { redCoin.Content() },
                @Composable { striker.Content() },

                @Composable { holes.forEach { it.Content() } }
            ),
            measurePolicy = NormalCoinsPlacementMeasurePolicy(
                angle, coins, striker, isOnStrikerMove, strikerXPercentage
            )
        )
    }
}

@Stable
class NormalCoinsPlacementMeasurePolicy(
    private val angle: Float,
    private val coins: List<Coin>,
    private val striker: Striker,
    private val isOnStrikerMove: Boolean,
    private val strikerXPercentage: Float
): MultiContentMeasurePolicy {

    private val whiteCoins = coins.filter { it.color == Color.White }
    private val blackCoins = coins.filter { it.color == Color.Black }
    private val redCoin = coins.first { it.color == Color.Red }

    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints
    ): MeasureResult {

        val whiteCoinsMeasurables = measurables[0]
        val blackCoinsMeasurables = measurables[1]
        val redCoinMeasurable = measurables[2]
        val strikerMeasurable = measurables[3]
        val holeMeasurables = measurables[4]

        val coinConstraints = Constraints.fixed(CoinSizeInDp.dp.roundToPx(), CoinSizeInDp.dp.roundToPx())
        val strikerConstraints = Constraints.fixed(StrikerSizeInDp.dp.roundToPx(), StrikerSizeInDp.dp.roundToPx())
        val holeConstraints = Constraints.fixed(HoleSizeInDp.dp.roundToPx(), HoleSizeInDp.dp.roundToPx())

        val whiteCoinsPlaceables = whiteCoinsMeasurables.map { it.measure(coinConstraints) }
        val blackCoinsPlaceables = blackCoinsMeasurables.map { it.measure(coinConstraints) }
        val redCoinPlaceable = redCoinMeasurable.map { it.measure(coinConstraints) }
        val strikerPlaceable = strikerMeasurable.map { it.measure(strikerConstraints) }
        val holePlaceables = holeMeasurables.map { it.measure(holeConstraints) }

        val size = IntSize(constraints.maxWidth, constraints.maxHeight)

        return layout(size.width, size.height) {
            if(notYetPositionedCoins()) {
                findCoinPositions(size)
            }

            holePlaceables.forEachIndexed { index, placeable ->
                val position = when(index) {
                    0 -> IntOffset(0, 0)
                    1 -> IntOffset(size.width - placeable.width, 0)
                    2 -> IntOffset(0, size.height - placeable.height)
                    else -> IntOffset(size.width - placeable.width, size.height - placeable.height)
                }
                placeable.place(position.x, position.y)
            }

            val pivot = size.center.toOffset()
            whiteCoinsPlaceables.forEachIndexed { index, placeable ->
                val coinPoint = whiteCoins[index].position.getRotatedOffset(angle, pivot).round()
                placeable.place(coinPoint.x - placeable.width/2, coinPoint.y - placeable.height/2)
            }
            blackCoinsPlaceables.forEachIndexed { index, placeable ->
                val coinPoint = blackCoins[index].position.getRotatedOffset(angle, pivot).round()
                placeable.place(coinPoint.x - placeable.width/2, coinPoint.y - placeable.height/2)
            }
            redCoinPlaceable.forEach {
                val coinPoint = redCoin.position.getRotatedOffset(angle, pivot).round()
                it.place(coinPoint.x - it.width/2, coinPoint.y - it.height/2)
            }
            strikerPlaceable.forEach {
                if(isOnStrikerMove) {
                    val dis = (size.width * 0.2f).roundToInt()
                    val position = Offset(
                        (lerp(dis, size.width - dis, strikerXPercentage)).toFloat(),
                        size.center.y + (.71f * size.center.y)
                    )
                    println(striker.position)
                    striker.updatePosition(position)
                }
                val coinPoint = striker.position.getRotatedOffset(angle, pivot).round()
                it.place(coinPoint.x - it.width/2, coinPoint.y - it.height/2)
            }
        }
    }

    private fun notYetPositionedCoins(): Boolean {
        return coins.any { it.position == Offset.Unspecified || striker.position == Offset.Unspecified }
    }

    private fun Density.findCoinPositions(size: IntSize) {
        val coinRadius = CoinSizeInDp.dp.roundToPx()/2
        val center = size.center
        val circles = listOf(
            Pair(2*coinRadius, 6),
            Pair(4*coinRadius, 12)
        )

        var whiteCoinsIndex = 0
        var blackCoinsIndex = 0

        circles.forEach { circle ->
            val angle = 2 * PI / circle.second

            var currentAngle = 0.0
            var index = 0
            while(index < circle.second) {
                if(index % 2 == 0) {
                    val x = center.x + (circle.first * cos(currentAngle)).toFloat()
                    val y = center.y + (circle.first * sin(currentAngle)).toFloat()
                    whiteCoins[whiteCoinsIndex].updatePosition(Offset(x,y))
                    whiteCoinsIndex++
                } else {
                    val x = center.x + (circle.first * cos(currentAngle)).roundToInt()
                    val y = center.y + (circle.first * sin(currentAngle)).roundToInt()
                    blackCoins[blackCoinsIndex].updatePosition(Offset(x.toFloat(), y.toFloat()))
                    blackCoinsIndex++
                }
                index++
                currentAngle += angle
            }
        }
        redCoin.updatePosition(Offset(center.x.toFloat(), center.y.toFloat()))
        val dis = (size.width * 0.2f).roundToInt()
        val position = Offset(
            (lerp(dis, size.width - dis, strikerXPercentage)).toFloat(),
            center.y + (.71f * center.y)
        )
        striker.updatePosition(position)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NormalCoinsPlacementMeasurePolicy) return false

        if (angle != other.angle) return false
        if (whiteCoins != other.whiteCoins) return false
        if (blackCoins != other.blackCoins) return false
        if (redCoin != other.redCoin) return false
        if (striker != other.striker) return false
        if (isOnStrikerMove != other.isOnStrikerMove) return false
        if (strikerXPercentage != other.strikerXPercentage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = angle.hashCode()
        result = 31 * result + whiteCoins.hashCode()
        result = 31 * result + blackCoins.hashCode()
        result = 31 * result + redCoin.hashCode()
        result = 31 * result + striker.hashCode()
        result = 31 * result + isOnStrikerMove.hashCode()
        result = 31 * result + strikerXPercentage.hashCode()
        return result
    }
}