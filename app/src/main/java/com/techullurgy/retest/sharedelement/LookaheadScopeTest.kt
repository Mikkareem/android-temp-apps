@file:OptIn(ExperimentalAnimatableApi::class)

package com.techullurgy.retest.sharedelement

import androidx.compose.animation.core.DeferredTargetAnimation
import androidx.compose.animation.core.ExperimentalAnimatableApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ApproachLayoutModifierNode
import androidx.compose.ui.layout.ApproachMeasureScope
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

private const val animationDuration = 1500

private val animationSpecForIntSize: FiniteAnimationSpec<IntSize> = tween(animationDuration, easing = FastOutSlowInEasing)
private val animationSpecForIntOffset: FiniteAnimationSpec<IntOffset> = tween(animationDuration, easing = FastOutSlowInEasing)

private class AnimatedPlacementModifierNode(
    var lookaheadScope: LookaheadScope
): ApproachLayoutModifierNode, Modifier.Node() {

    val offsetAnimation
            = DeferredTargetAnimation(IntOffset.VectorConverter)

    val sizeAnimation
            = DeferredTargetAnimation(IntSize.VectorConverter)

    override fun isMeasurementApproachInProgress(lookaheadSize: IntSize): Boolean {
        sizeAnimation.updateTarget(
            lookaheadSize,
            coroutineScope,
            animationSpecForIntSize
        )

        return !sizeAnimation.isIdle
    }

    override fun Placeable.PlacementScope.isPlacementApproachInProgress(
        lookaheadCoordinates: LayoutCoordinates
    ): Boolean {
        val target = with(lookaheadScope) {
            lookaheadScopeCoordinates.localLookaheadPositionOf(lookaheadCoordinates).round()
        }

        offsetAnimation.updateTarget(
            target,
            coroutineScope,
            animationSpecForIntOffset
        )

        return !offsetAnimation.isIdle
    }

    override fun ApproachMeasureScope.approachMeasure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {

        val currentSizeAnimation = sizeAnimation.updateTarget(
            lookaheadSize,
            coroutineScope,
            animationSpecForIntSize
        )

        val width = currentSizeAnimation.width
        val height = lookaheadSize.height

        val animatedConstraints = Constraints.fixed(width, height)

        val placeable = measurable.measure(animatedConstraints)

        return layout(placeable.width, placeable.height) {
            val coordinates = coordinates
            if (coordinates != null) {
                // Calculates the target offset within the lookaheadScope
                val target =
                    with(lookaheadScope) {
                        lookaheadScopeCoordinates.localLookaheadPositionOf(coordinates).round()
                    }

                // Uses the target offset to start an offset animation
                val animatedOffset = offsetAnimation.updateTarget(
                    target,
                    coroutineScope,
                    animationSpecForIntOffset
                )
                // Calculates the *current* offset within the given LookaheadScope
                val placementOffset =
                    with(lookaheadScope) {
                        lookaheadScopeCoordinates
                            .localPositionOf(coordinates, Offset.Zero)
                            .round()
                    }
                // Calculates the delta between animated position in scope and current
                // position in scope, and places the child at the delta offset. This puts
                // the child layout at the animated position.
                val (x, y) = animatedOffset - placementOffset
                placeable.place(x, y)
            } else {
                placeable.place(0, 0)
            }
        }
    }
}

// Creates a custom node element for the AnimatedPlacementModifierNode above.
private data class AnimatePlacementNodeElement(val lookaheadScope: LookaheadScope) :
    ModifierNodeElement<AnimatedPlacementModifierNode>() {

    override fun update(node: AnimatedPlacementModifierNode) {
        node.lookaheadScope = lookaheadScope
    }

    override fun create(): AnimatedPlacementModifierNode {
        return AnimatedPlacementModifierNode(lookaheadScope)
    }
}

private fun Modifier.animatePlacement(lookaheadScope: LookaheadScope): Modifier
    = this then AnimatePlacementNodeElement(lookaheadScope)

@Preview
@Composable
private fun LookaheadScopeTest() {
    var state by remember {
        mutableStateOf<ColumnState>(ColumnState.SingleColumn)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        RadioGroup(
            selectedOption = state,
            options = listOf(
                ColumnState.SingleColumn,
                ColumnState.DoubleColumn,
                ColumnState.TripleColumn
            ),
            onSelectedChange = { state = it },
            label = {
                Text(it.name)
            }
        )

        LookaheadScope {
            LazyVerticalGrid(
                modifier = Modifier.weight(1f).background(Color.White.copy(0.4f)),
                columns = GridCells.Fixed(state.count),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(colors) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .animatePlacement(this@LookaheadScope)
                            .clip(RoundedCornerShape(20))
                            .background(it)
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> RadioGroup(
    options: List<T>,
    selectedOption: T,
    onSelectedChange: (T) -> Unit,
    modifiers: List<Modifier> = emptyList(),
    label: (@Composable (T) -> Unit)? = null
) {
    check(modifiers.isEmpty() || modifiers.size == options.size)

    options.forEachIndexed { index, option ->
        Row(
            modifier = if(index < modifiers.size) modifiers[index] else Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedOption == option,
                onClick = { onSelectedChange(option) }
            )
            label?.let {
                Spacer(Modifier.width(8.dp))
                it(option)
            }
        }
    }
}


sealed class ColumnState(val name: String, val count: Int) {
    data object SingleColumn: ColumnState("Single Column", 1)
    data object DoubleColumn: ColumnState("Double Column", 2)
    data object TripleColumn: ColumnState("Triple Column", 3)
}

val colors = listOf(
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Black,
    Color.Magenta,
    Color.Cyan,
    Color(0xff8e782f),
    Color(0xff8e2f71),
    Color(0xff198e2f),
)