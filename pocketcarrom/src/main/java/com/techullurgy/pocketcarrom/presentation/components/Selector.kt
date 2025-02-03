package com.techullurgy.pocketcarrom.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun Selector(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints.fixedHeight(40.dp.roundToPx()).copy(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth
                    )
                )

                layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val newValue = change.position.x/size.width
                    if(newValue in 0f..1f) {
                        onValueChange(newValue)
                    }
                }
            }
    ) {
        val originalBarSizeHeight = size.height * 0.35f
        val originalBarTopLeft = Offset(
            x = 0f,
            y = originalBarSizeHeight
        )
        val originalBarSize = Size(size.width, originalBarSizeHeight)

        val valueBarSizeWidth = size.width * value
        val valueBarSize = Size(valueBarSizeWidth, originalBarSizeHeight)

        drawRoundRect(
            color = Color.LightGray,
            cornerRadius = CornerRadius(20.dp.toPx()),
            topLeft = originalBarTopLeft,
            size = originalBarSize
        )

        drawRoundRect(
            color = Color.Blue,
            cornerRadius = CornerRadius(20.dp.toPx()),
            topLeft = originalBarTopLeft,
            size = valueBarSize
        )

        drawCircle(
            color = Color.Red,
            radius = size.height/2f,
            center = center.copy(
                x = size.width * value
            )
        )
    }
}