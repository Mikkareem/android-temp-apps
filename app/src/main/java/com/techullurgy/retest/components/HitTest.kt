package com.techullurgy.retest.components

import android.media.MediaMetadataRetriever
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun HitTest() {
    Box(
        Modifier.then(HitTestElement("Parent"))
    ) {
        Box(
            Modifier
                .size(200.dp)
                .then(HitTestElement("First"))
                .background(Color.Green)
        )

        Box(
            Modifier
                .size(200.dp)
                .then(HitTestElement("Second"))
                .background(Color.Red)
        )
    }
}

private class HitTestElement(private val tag: String)
    : NonUpdatedModifierNodeElement<HitTestNode>()
{
    override fun create(): HitTestNode = HitTestNode(tag)
}

private class HitTestNode(private val tag: String)
    : PointerInputModifierNode,
    Modifier.Node()
{
    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        println("Tag: [$tag], Event.Type: [${pointerEvent.type}], Pass: [$pass]")
    }

    override fun onCancelPointerInput() {
        MediaMetadataRetriever.METADATA_KEY_AUTHOR
        TODO("Not yet implemented")
    }
}