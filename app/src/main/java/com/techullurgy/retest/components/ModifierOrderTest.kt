package com.techullurgy.retest.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints

@Preview
@Composable
fun ModifierOrderTest() {
    var show by remember { mutableStateOf(false) }

    Box(
        Modifier.fillMaxSize(),
        Alignment.Center
    ) {
        Box(
            Modifier
                .testNode("Parent1").testNode("Parent2")
                .testNode("Parent3").testNode("Parent4")
        ) {
            Box(
                Modifier
                    .testNode("Child1").testNode("Child2")
                    .testNode("Child3").testNode("Child4")
            ) {
                Box(
                    Modifier
                        .testNode("GrandChild1").testNode("GrandChild2")
                        .testNode("GrandChild3").testNode("GrandChild4")
                ) {
                    Box(
                        Modifier
                            .testNode("GrandGrandChild1")
                            .testNode("GrandGrandChild2")
                            .testNode("GrandGrandChild3")
                            .testNode("GrandGrandChild4")
                    )
                }
            }
        }
    }
}

class TestModifierNode(private val tag: String): LayoutModifierNode, Modifier.Node() {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        println("[$tag] Received Constraints MaxWidth: [${constraints.maxWidth}]")
        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth - 20
            )
        )
        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}
class TestModifierNodeElement(private val tag: String)
    : NonUpdatedModifierNodeElement<TestModifierNode>() {
    override fun create(): TestModifierNode = TestModifierNode(tag)
}
fun Modifier.testNode(tag: String): Modifier = this then TestModifierNodeElement(tag)