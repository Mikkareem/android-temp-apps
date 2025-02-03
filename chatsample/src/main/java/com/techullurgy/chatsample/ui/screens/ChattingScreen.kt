package com.techullurgy.chatsample.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.snap
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.techullurgy.chatsample.ui.viewmodels.ChatMessage
import com.techullurgy.chatsample.ui.viewmodels.ChattingScreenAction
import com.techullurgy.chatsample.ui.viewmodels.ChattingScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNot
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun ChattingScreen(
    viewModel: ChattingScreenViewModel = remember { ChattingScreenViewModel() }
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    val screenTitle = screenState.message.map { if(it.sender == "OWNER") it.receiver else it.sender }.toSet().first()

    val isKeyboardOpen = WindowInsets.isImeVisible

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(Color.Green)
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            Text(
                text = screenTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(screenState.message) {
                val arrangement = if(it.sender == "OWNER") Arrangement.End else Arrangement.Start
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = arrangement
                ) {
                    MessageContent(
                        message = it,
                        onReply = {
                            viewModel.onAction(ChattingScreenAction.OnReplySelectionMessageChange(it))
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            val bottomPadding = if(isKeyboardOpen) 24.dp else 0.dp
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(bottom = bottomPadding)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Green, RoundedCornerShape(24.dp))
                        .padding(8.dp)
                ) {
                    AnimatedVisibility(
                        visible = screenState.replySelectionMessage != null,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            screenState.replySelectionMessage?.let {
                                ReplyRefBox(
                                    chatMessage = it,
                                    onClose = {
                                        viewModel.onAction(ChattingScreenAction.OnReplySelectionMessageClose)
                                    }
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    MessageBox(
                        text = screenState.typedText,
                        onTextChange = {
                            viewModel.onAction(ChattingScreenAction.OnTypingTextChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .drawBehind {
                            drawCircle(
                                color = Color.Red
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.Yellow
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBox(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        propagateMinConstraints = true
    ) {

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            cursorBrush = SolidColor(Color.Magenta),
            modifier = modifier
                .requiredHeightIn(min = 36.dp)
        ) {
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if(text.isEmpty()) {
                    Text(" Message", fontSize = 18.sp)
                }
                it()
            }
        }
    }

}

@Composable
fun ReplyRefBox(
    chatMessage: ChatMessage,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.Magenta.copy(0.6f),
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (chatMessage.sender == "OWNER") "You" else chatMessage.sender,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow
                )

                Box(
                    modifier = Modifier.clickable { onClose() }
                ) {
                    Text(
                        text = "X",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    )
                }

            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = chatMessage.content,
                maxLines = 2,
                color = Color.White,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

enum class SwipeToReplyValue {
    Resting, Replying
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageContent(
    message: ChatMessage,
    onReply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val anchors = remember(LocalDensity.current) {
        val replyOffset = with(density) { 48.dp.toPx() }
        DraggableAnchors {
            SwipeToReplyValue.Resting at 0f
            SwipeToReplyValue.Replying at replyOffset
        }
    }
    val dragState = remember(anchors) {
        AnchoredDraggableState(
            initialValue = SwipeToReplyValue.Resting,
            anchors = anchors,
            positionalThreshold = { it },
            velocityThreshold = { 0f },
            snapAnimationSpec = snap(),
            decayAnimationSpec = splineBasedDecay(density)
        )
    }

    LaunchedEffect(Unit) {
        snapshotFlow { dragState.settledValue }
            .drop(1)
            .distinctUntilChanged()
            .filterNot { it == SwipeToReplyValue.Resting }
            .collectLatest {
                delay(100)
                onReply()
                dragState.animateTo(SwipeToReplyValue.Resting)
            }
    }

    val progress = dragState.progress(SwipeToReplyValue.Resting, SwipeToReplyValue.Replying)

    val overscrollEffect = ScrollableDefaults.overscrollEffect()
    Box(
        modifier = modifier
            .fillMaxWidth(0.6f)
            .wrapContentWidth(
                align = if (message.sender == "OWNER") Alignment.End else Alignment.Start
            )
    ) {
        if(progress != 0f) {
            Box(
                Modifier
                    .alpha(progress)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
        Text(
            text = message.content,
            modifier = Modifier
                .overscroll(overscrollEffect)
                .offset {
                    IntOffset(
                        x = dragState
                            .requireOffset()
                            .roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    overscrollEffect = overscrollEffect
                )
                .drawBehind {
                    drawRoundRect(
                        color = Color.Yellow,
                        cornerRadius = CornerRadius(10.dp.toPx()),
                    )
                }
                .padding(8.dp)
        )
    }
}