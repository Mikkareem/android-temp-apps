package com.techullurgy.retest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

@Preview
@Composable
fun LayoutCoordinatesTest() {
    var localToScreenOffset by remember { mutableStateOf(Offset.Zero) }
    var screenToLocalOffset by remember { mutableStateOf(Offset.Zero) }
    var localPositionFromRootCoordOffset by remember { mutableStateOf(Offset.Zero) }
    var localBoundingBox by remember { mutableStateOf(Rect.Zero) }

    var rootCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val density = LocalDensity.current

    Scaffold {
        Box(
            Modifier.fillMaxSize().padding(it)
        ) {
            Box(
                Modifier.onGloballyPositioned { rootCoordinates = it }
                    .align(Alignment.Center).size(400.dp).border(3.dp, Color.Blue)
            ) {
                TempBox(
//                    modifier = Modifier.align(Alignment.Center)
                ) {
                    localToScreenOffset = it.localToScreen(Offset.Zero)
                    screenToLocalOffset = it.screenToLocal(Offset.Zero)
                    rootCoordinates?.let { _ ->
                        localPositionFromRootCoordOffset = it.localPositionOf(
                            rootCoordinates!!,
                            rootCoordinates!!.run { Offset(size.width/2f, size.height/2f) }
                        )
                    }

                    rootCoordinates?.let { _ ->
                        localBoundingBox = it.localBoundingBoxOf(
                            rootCoordinates!!
                        ).run {
                            with(density) {
                                Rect(
                                    left = left.toDp().value,
                                    right = right.toDp().value,
                                    top = top.toDp().value,
                                    bottom = bottom.toDp().value,
                                )
                            }
                        }
                    }
                }
            }

            Column(
                Modifier.align(Alignment.Center)
            ) {
                Text("Local to Root: $localToScreenOffset")
                Text("Screen to Local: $screenToLocalOffset")
                Text("Local position from Root coord: $localPositionFromRootCoordOffset")
                Text("Local bounding box: $localBoundingBox")
            }
        }
    }
}

@Composable
private fun TempBox(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    color: Color = Color.Black,
    onPositionChange: (LayoutCoordinates) -> Unit
) {
    TempBox(
        modifier = modifier,
        width = size,
        height = size,
        color = color,
        onPositionChange = onPositionChange
    )
}

@Composable
private fun TempBox(
    modifier: Modifier = Modifier,
    width: Dp = 50.dp,
    height: Dp = 50.dp,
    color: Color = Color.Black,
    onPositionChange: (LayoutCoordinates) -> Unit
) {
    var offset by remember { mutableStateOf(IntOffset.Zero) }

    Box(
        modifier = modifier
            .offset { offset }
            .width(width)
            .height(height)
            .onPlaced(onPositionChange)
            .background(color)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    offset += dragAmount.round()
                }
            }
    )
}