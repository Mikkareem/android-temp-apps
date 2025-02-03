package com.techullurgy.openglsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.viewinterop.AndroidView
import com.techullurgy.openglsample.ui.theme.RetestTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private val GearColor = Color.White

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(innerPadding)
                    ) {
                        var surfaceView by remember {
                            mutableStateOf<OpenGLSurfaceView?>(null)
                        }

                        var xAngle by remember { mutableFloatStateOf(0f) }
                        var yAngle by remember { mutableFloatStateOf(0f) }
                        var zAngle by remember { mutableFloatStateOf(0f) }

                        AndroidView(
                            factory = {
                                OpenGLSurfaceView(context = it).also { view ->
                                    surfaceView = view
                                }
                            },
                            onReset = {
                                surfaceView = it
                            },
                            onRelease = {
                                surfaceView = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(32.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Gear(
                                    degree = xAngle,
                                    onDegreeChange = {
                                        xAngle = it
                                        surfaceView?.rotateCameraX(it)
                                    },
                                )
                                Gear(
                                    degree = yAngle,
                                    onDegreeChange = {
                                        yAngle = it
                                        surfaceView?.rotateCameraY(it)
                                    },
                                )
                                Gear(
                                    degree = zAngle,
                                    onDegreeChange = {
                                        zAngle = it
                                        surfaceView?.rotateCameraZ(it)
                                    },
                                )
                            }

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ProvideTextStyle(LocalTextStyle.current.copy(color = Color.White)) {
                                    Control("F", { surfaceView?.xTurn(-1, -1) })
                                    Control("F'", { surfaceView?.xTurn(-1, 1) })
                                    Control("U", { surfaceView?.yTurn(-1, -1) })
                                    Control("U'", { surfaceView?.yTurn(-1, 1) })
                                    Control("L", { surfaceView?.zTurn(-1, 1) })
                                    Control("L'", { surfaceView?.zTurn(-1, -1) })

                                    Control("B", { surfaceView?.xTurn(1, -1) })
                                    Control("B'", { surfaceView?.xTurn(1, 1) })
                                    Control("D", { surfaceView?.yTurn(1, -1) })
                                    Control("D'", { surfaceView?.yTurn(1, 1) })
                                    Control("R", { surfaceView?.zTurn(1, 1) })
                                    Control("R'", { surfaceView?.zTurn(1, -1) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Control(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.Magenta)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(name, fontSize = 24.sp)
    }
}

@Composable
private fun Gear(
    degree: Float,
    onDegreeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onDegreeChange(0f) },
                    onDragCancel = { onDegreeChange(0f) }
                ) { change, _ ->
                    val translatedPos = change.position - size.center.toOffset()
                    val theta = atan2(translatedPos.y, translatedPos.x) * (180f / (22f/7f))
                    onDegreeChange(theta)
                }
            }
            .drawBehind {
                drawCircle(
                    color = Color.Red,
                    style = Stroke(10.dp.toPx())
                )

                if(degree != 0f) {
                    val unitX = cos(Math.toRadians(degree.toDouble())).toFloat()
                    val unitY = sin(Math.toRadians(degree.toDouble())).toFloat()

                    val scaleFactor = 150f

                    val pos = Offset(unitX * scaleFactor, unitY * scaleFactor)

                    val path = Path().apply {
                        moveTo(center.x, center.y)
                        relativeLineTo(pos.x, pos.y)
                    }

                    drawPath(
                        path = path,
                        style = Stroke(7.dp.toPx()),
                        color = GearColor
                    )

                    drawCircle(
                        color = GearColor,
                        center = center+pos,
                        radius = 15.dp.toPx()
                    )
                } else {
                    drawCircle(
                        color = GearColor,
                        radius = 15.dp.toPx()
                    )
                }
            }
    )
}