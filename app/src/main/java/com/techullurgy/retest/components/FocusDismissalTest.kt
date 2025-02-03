package com.techullurgy.retest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.traverseDescendants
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Objects


@Preview
@Composable
fun FocusDismissalTest() {
    Column(
        modifier = Modifier.fillMaxSize().imePadding().then(FocusDismissalParentElement())
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(30) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Red)
                ) {
                    Text(
                        text = it.toString(),
                        fontSize = 20.sp
                    )
                }
            }
        }
        var value1 by remember { mutableStateOf("") }
        var value2 by remember { mutableStateOf("") }

        Row {
            OutlinedTextField(
                value = value1,
                onValueChange = { value1 = it },
                modifier = Modifier.then(FocusableTraverseElement())
            )
            IconButton({
                println("Sent....")
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, null)
            }
        }

        Row {
            OutlinedTextField(
                value = value2,
                onValueChange = { value2 = it },
                modifier = Modifier.then(FocusableTraverseElement())
            )
            IconButton({
                println("Sent....")
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, null)
            }
        }
    }
}

class FocusDismissalParentNode: PointerInputModifierNode, GlobalPositionAwareModifierNode, DelegatingNode() {
    private var rootCoordinates: LayoutCoordinates? = null

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        rootCoordinates = coordinates
    }

    override fun onCancelPointerInput() {
        undelegate(pointerEventCallback)
    }

    private val pointerEventCallback = SuspendingPointerInputModifierNode {
        awaitEachGesture {
            val change = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Final)
            try {
                val upOrCancel = withTimeout(viewConfiguration.longPressTimeoutMillis) {
                    waitForUpOrCancellation(pass = PointerEventPass.Final)
                }

                upOrCancel?.let {
                    // Press detected in initial Phase
                    if(clearChildFocus(change.position)) {
                        it.consume()
                    }
                }
            } catch (e: PointerEventTimeoutCancellationException) {}
        }
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        delegate(pointerEventCallback).onPointerEvent(pointerEvent, pass, bounds)
    }

    private fun clearChildFocus(position: Offset): Boolean {
        var handled = false
        traverseDescendants("FocusableTraverse") {
            if(it is FocusableTraverseNode) {
                if(it.isCurrentlyFocusing()) {
                    handled = it.clearFocus(rootCoordinates!!, position)
                    TraversableNode.Companion.TraverseDescendantsAction.CancelTraversal
                } else {
                    TraversableNode.Companion.TraverseDescendantsAction.ContinueTraversal
                }
            } else {
                TraversableNode.Companion.TraverseDescendantsAction.ContinueTraversal
            }
        }
        return handled
    }
}

class FocusableTraverseNode:
    FocusEventModifierNode,
    TraversableNode,
    CompositionLocalConsumerModifierNode,
    GlobalPositionAwareModifierNode,
    Modifier.Node()
{
    private var isCurrentlyFocusing = false

    private var currentCoordinates: LayoutCoordinates? = null

    override val traverseKey: Any
        get() = "FocusableTraverse"

    override fun onFocusEvent(focusState: FocusState) {
        isCurrentlyFocusing = focusState.isFocused
    }

    fun clearFocus(rootCoordinates: LayoutCoordinates, offset: Offset): Boolean {
        if(!isCurrentlyFocusing()) return false

        currentCoordinates?.localPositionOf(rootCoordinates, Offset.Zero)

        return currentCoordinates?.boundsInRoot()?.contains(offset)?.let {
            if(!it) {
                currentValueOf(LocalFocusManager).clearFocus()
                true
            } else false
        } ?: false
    }

    fun isCurrentlyFocusing(): Boolean {
        return isCurrentlyFocusing
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        currentCoordinates = coordinates
    }
}

abstract class NonUpdatedModifierNodeElement<T: Modifier.Node> : ModifierNodeElement<T>() {
    override fun equals(other: Any?): Boolean = true
    override fun hashCode(): Int = Objects.hashCode(this)
    override fun update(node: T) = Unit
}

class FocusableTraverseElement: NonUpdatedModifierNodeElement<FocusableTraverseNode>() {
    override fun create(): FocusableTraverseNode = FocusableTraverseNode()
}

class FocusDismissalParentElement: NonUpdatedModifierNodeElement<FocusDismissalParentNode>() {
    override fun create(): FocusDismissalParentNode = FocusDismissalParentNode()
}