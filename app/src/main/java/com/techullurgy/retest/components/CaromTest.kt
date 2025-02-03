package com.techullurgy.retest.components

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.util.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun CaromTest() {
    val coinSize = 18.dp

    val whiteCoins = remember {
        List(9) { Coin(coinSize, Color.White) }
    }

    val blackCoins = remember {
        List(9) { Coin(coinSize, Color.Black) }
    }

    val redCoin = remember { Coin(coinSize, Color.Red) }
    val striker = remember { Striker(coinSize) }

    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        while(true) {
            withInfiniteAnimationFrameMillis {
                Coin.collisionResolution(density)
                whiteCoins.forEach { it.update() }
                blackCoins.forEach { it.update() }
                redCoin.update()
                striker.update()
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        Alignment.Center
    ) {
        var strikerXPercentage by remember { mutableFloatStateOf(0.5f) }

        Column {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = 0f
                    }
                    .background(Color.DarkGray)
                    .padding(24.dp)
            ) {
                Layout(
                    contents = listOf(
                        @Composable { whiteCoins.forEach { it.content() } },
                        @Composable { blackCoins.forEach { it.content() } },
                        @Composable { redCoin.content() },
                        @Composable { striker.content() }
                    ),
                    measurePolicy = InitialCoinsPlacementMeasurePolicy(
                        coinSize,
                        strikerXPercentage
                    ),
                    modifier = Modifier
                        .background(Color(0xffa27a4d))
                        .drawBehind { drawBoard() }
                        .clickable { striker.strike() }
                )
            }

            Spacer(Modifier.height(16.dp))

            Slider(
                value = strikerXPercentage,
                onValueChange = { strikerXPercentage = it }
            )
        }
    }
}

class InitialCoinsPlacementMeasurePolicy(
    private val coinSize: Dp,
    private val strikerXPercentage: Float
): MultiContentMeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<List<Measurable>>,
        constraints: Constraints
    ): MeasureResult {
        val whiteCoinsMeasurables = measurables[0]
        val blackCoinsMeasurables = measurables[1]
        val redCoinMeasurable = measurables[2]
        val strikerMeasurable = measurables[3]

        val whiteCoinsPlaceables = whiteCoinsMeasurables.map { it.measure(constraints) }
        val blackCoinsPlaceables = blackCoinsMeasurables.map { it.measure(constraints) }
        val redCoinPlaceable = redCoinMeasurable.map { it.measure(constraints) }
        val strikerPlaceable = strikerMeasurable.map { it.measure(constraints) }

        val size = minOf(constraints.maxWidth, constraints.maxHeight)

        return layout(size, size) {
            val coinRadius = coinSize.roundToPx()/2
            val center = IntOffset(size/2, size/2)

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
                        val whiteCoinPlaceable = whiteCoinsPlaceables[whiteCoinsIndex]
                        val x = center.x + (circle.first * cos(currentAngle)).roundToInt() - coinRadius
                        val y = center.y + (circle.first * sin(currentAngle)).roundToInt() - coinRadius
                        whiteCoinPlaceable.place(x, y)
                        whiteCoinsIndex++
                    } else {
                        val blackCoinPlaceable = blackCoinsPlaceables[blackCoinsIndex]
                        val x = center.x + (circle.first * cos(currentAngle)).roundToInt() - coinRadius
                        val y = center.y + (circle.first * sin(currentAngle)).roundToInt() - coinRadius
                        blackCoinPlaceable.place(x, y)
                        blackCoinsIndex++
                    }
                    index++
                    currentAngle += angle
                }
            }
            redCoinPlaceable.forEach {
                it.place(center.x - coinRadius, center.y - coinRadius)
            }
            strikerPlaceable.forEach {
                val dis = (size * 0.2f).roundToInt()
                it.place(
                    lerp(dis, size - dis, strikerXPercentage) - coinRadius,
                    center.y - coinRadius + (.71f * center.y).roundToInt()
                )
            }
        }
    }
}

open class Coin(
    private val coinSize: Dp,
    private val color: Color,
) {
    private var position by mutableStateOf(Offset.Zero)

    private var boardLayoutCoordinates: LayoutCoordinates? = null

    private var coinLayoutCoordinates: LayoutCoordinates? = null

    init {
        createdCoins.add(this)
    }

    private val mass = Random.nextDouble(2.0,8.0)

    open val content = @Composable {
        Box(
            Modifier
                .offset { position.round() }
                .onPlaced {
                    coinLayoutCoordinates = it
                    boardLayoutCoordinates = it.parentLayoutCoordinates
                }
                .size(coinSize)
                .drawBehind { draw() }
        )
    }

    protected open fun DrawScope.draw() {
        drawCircle(color)
    }

    protected var velocity = Offset.Zero

    fun update() {
        if(abs(velocity.x) < 0.1f && abs(velocity.y) < 0.1f) {
            velocity = Offset.Zero
        } else {
            edgeDetection()
            position += velocity
            velocity *= 0.96f // (Friction / Brake)
        }
    }

    private fun edgeDetection() {
        val relativeBox = boardLayoutCoordinates!!.localBoundingBoxOf(coinLayoutCoordinates!!)
        val boardWidth = boardLayoutCoordinates!!.size.width
        val boardHeight = boardLayoutCoordinates!!.size.height

        if(relativeBox.left < 0f || relativeBox.right > boardWidth) {
            velocity = velocity.copy(x = velocity.x * -1f)
        }
        if(relativeBox.top < 0f || relativeBox.bottom > boardHeight) {
            velocity = velocity.copy(y = velocity.y * -1f)
        }
    }

    private fun collide(other: Coin, density: Density) {
        val otherPosition = other.boardLayoutCoordinates!!.localPositionOf(
            other.coinLayoutCoordinates!!,
            Offset.Zero
        )
        val currPosition = boardLayoutCoordinates!!.localPositionOf(
            coinLayoutCoordinates!!,
            Offset.Zero
        )
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

    companion object {
        private val createdCoins: MutableList<Coin> = mutableListOf()

        private fun Offset.dot(other: Offset): Float {
            return x * other.x + y * other.y
        }

        private fun Offset.setMag(newMag: Float): Offset {
            val currentMag = mag()
            val magnitudeRatio = newMag / currentMag
            return Offset(
                x = x * magnitudeRatio,
                y = y * magnitudeRatio
            )
        }

        private fun Offset.mag(): Float = sqrt(x * x + y * y)

        fun collisionResolution(density: Density) {
            for(i in 0 until createdCoins.size-1) {
                val currentLeft = createdCoins[i]
                for(j in i+1 until createdCoins.size) {
                    val currentRight = createdCoins[j]
                    currentLeft.collide(currentRight, density)
                }
            }
        }
    }
}

class Striker(
    coinSize: Dp,
): Coin(coinSize*1.3f, Color.Green) {
    override fun DrawScope.draw() {
        drawCircle(Color.Yellow)

        drawCircle(
            color = Color.Blue,
            radius = (size.width - center.x) * .5f
        )
    }

    fun strike() {
        velocity += Offset(
            x = Random.nextDouble(-10.0, 60.0).toFloat(),
            y = Random.nextDouble(-40.0, -20.0).toFloat()
        )
    }
}

private fun DrawScope.drawBoard() {
    // Center
    drawCircle(
        color = Color.Black,
        style = Stroke(2.dp.toPx()),
        radius = size.width * 0.15f
    )
    drawCircle(
        color = Color.Magenta,
        style = Stroke(2.dp.toPx()),
        radius = size.width * 0.025f
    )


    // Holes
    val holeRadius = size.width * .055f
    drawCircle(
        color = Color.Black,
        radius = holeRadius,
        center = Offset(
            holeRadius,
            holeRadius
        )
    )
    drawCircle(
        color = Color.Black,
        radius = holeRadius,
        center = Offset(
            size.width - holeRadius,
            holeRadius
        )
    )
    drawCircle(
        color = Color.Black,
        radius = holeRadius,
        center = Offset(
            holeRadius,
            size.height - holeRadius
        )
    )
    drawCircle(
        color = Color.Black,
        radius = holeRadius,
        center = Offset(
            size.width - holeRadius,
            size.height - holeRadius
        )
    )

    // Minuses
    val minusPath = Path().apply {
        moveTo(2 * holeRadius + 2.dp.toPx(), 2 * holeRadius + 2.dp.toPx())
        relativeLineTo(50.dp.toPx(), 50.dp.toPx())
        addOval(
            oval = Rect(
                center = Offset(
                    2 * holeRadius + 2.dp.toPx() + 15.dp.toPx(),
                    2 * holeRadius + 2.dp.toPx() + 15.dp.toPx()
                ),
                radius = 5.dp.toPx()
            )
        )
        addArc(
            oval = Rect(
                center = Offset(
                    2 * holeRadius + 2.dp.toPx() + 40.dp.toPx(),
                    2 * holeRadius + 2.dp.toPx() + 40.dp.toPx()
                ),
                radius = holeRadius * 0.8f
            ),
            startAngleDegrees = 250f,
            sweepAngleDegrees = 300f
        )
    }

    val eachAngle = 360f / 4
    repeat(4) {
        rotate(eachAngle * it) {
            drawPath(
                path = minusPath,
                color = Color.Black,
                style = Stroke(2.dp.toPx())
            )
            // StrikeArea
            drawLine(
                color = Color.Black,
                start = Offset(
                    size.width * .2f,
                    2 * holeRadius
                ),
                end = Offset(
                    size.width - (size.width * .2f),
                    2 * holeRadius
                ),
                strokeWidth = 4.dp.toPx()
            )

            drawLine(
                color = Color.Black,
                start = Offset(
                    size.width * .2f,
                    2 * holeRadius + 15.dp.toPx()
                ),
                end = Offset(
                    size.width - (size.width * .2f),
                    2 * holeRadius + 15.dp.toPx()
                ),
                strokeWidth = 2.dp.toPx()
            )
        }
    }

    repeat(4) {
        rotate(eachAngle * it) {
            drawCircle(
                color = Color.Red,
                style = Stroke(4.dp.toPx()),
                center = Offset(
                    size.width * 0.2f,
                    size.height * 0.13f
                ),
                radius = .5f * 15.dp.toPx()
            )
            drawCircle(
                color = Color.Red,
                style = Stroke(4.dp.toPx()),
                center = Offset(
                    size.width * 0.13f,
                    size.height * 0.2f
                ),
                radius = .5f * 15.dp.toPx()
            )
        }
    }
}

private fun Offset.getRotatedPoint(angle: Float): Offset {
    val angleInRadians = Math.toRadians(angle.toDouble())
    val c = cos(angleInRadians).toFloat()
    val s = sin(angleInRadians).toFloat()

    return Offset(
        x = x*c - y*s,
        y = x*s + y*c
    )
}

@Preview
@Composable
fun CaromTest2() {
    var coinCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    var location by remember { mutableStateOf("Hi") }
    var rotLocation by remember { mutableStateOf("Rotated") }

    var position by remember { mutableStateOf(IntOffset.Zero) }

    var rotatedPosition by remember { mutableStateOf(IntOffset(Int.MAX_VALUE, Int.MAX_VALUE)) }

    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Layout(
            modifier = Modifier
                .size(300.dp)
                .background(Color.Green),

            content = {
                Box(
                    modifier = Modifier
                        .offset { position }
                        .onPlaced {
                            coinCoordinates = it
                            val parent = it.parentCoordinates!!

                            val localOffset = it.localPositionOf(
                                parent,
                                parent.size.center.toOffset() - Offset(it.size.width/2f, it.size.height/2f)
                            )
                            val rotatedLocation = localOffset.getRotatedPoint(180f)

                            it.localPositionOf(
                                parent,
                                Offset(it.size.width/2f, it.size.height/2f)
                            )

                            location = "$localOffset"
                            rotLocation = "$rotatedLocation"

                            rotatedPosition = rotatedLocation.round()
                        }
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) {
                                placeable.place(-placeable.width/2, -placeable.height/2)
                            }
                        }
                        .clip(CircleShape)
                        .background(Color.Red)
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                position += dragAmount.round()
                            }
                        }
                )

                if(rotatedPosition.x != Int.MAX_VALUE) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                layout(placeable.width, placeable.height) {
                                    placeable.place(rotatedPosition.x-placeable.width/2, rotatedPosition.y-placeable.height/2)
                                }
                            }
                            .background(Color.Blue)
                    )
                }

                Column {
                    Text(
                        text = location,
                        fontSize = 20.sp,
                    )
                    Text(
                        text = rotLocation,
                        fontSize = 20.sp,
                    )
                }
            }
        ) { measurables, constraints ->
            val placeables = measurables.mapIndexed { index, measurable ->
                val coinSize = 21.dp.roundToPx()
                if(index == 0) {
                    val newConstraints = Constraints.fixed(coinSize, coinSize)

                    measurable.measure(newConstraints)
                } else {
                    if(index == 1 && rotatedPosition.x != Int.MAX_VALUE) {
                        val newConstraints = Constraints.fixed(coinSize, coinSize)
                        measurable.measure(newConstraints)
                    } else {
                        measurable.measure(constraints)
                    }
                }
            }

            val size = IntSize(constraints.maxWidth, constraints.maxHeight)

            layout(size.width, size.height) {
                placeables.forEachIndexed { index, placeable ->
                    if(index == 0) {
                        placeable.place(size.width/2, size.height/2)
                    } else {
                        if(index == 1 && rotatedPosition.x != Int.MAX_VALUE) {
                            placeable.place(size.width/2, size.height/2)
                        } else {
                            placeable.place(0, 0)
                        }
                    }
                }
            }
        }
    }
}

/*
*
* */