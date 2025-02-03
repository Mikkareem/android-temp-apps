package com.techullurgy.chatsample.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class ChattingScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChattingScreenState())
    val state = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                message = listOf(
                    ChatMessage(
                        sender = "OWNER",
                        receiver = "Riyas",
                        content = "Hi, da! How are you?"
                    ),
                    ChatMessage(
                        sender = "OWNER",
                        receiver = "Riyas",
                        content = "Sit hendrerit purus mi torquent; curae molestie turpis ultrices auctor. Faucibus risus quam ante congue eros nec vel mi. Facilisis conubia porttitor facilisi quisque duis proin sollicitudin pellentesque. Curae convallis felis,"
                    ),
                    ChatMessage(
                        sender = "OWNER",
                        receiver = "Riyas",
                        content = "Nam euismod ipsum fermentum potenti non habitasse. Aptent penatibus eget convallis vehicula enim pharetra dolor habitasse. Dis felis odio laoreet porta nostra urna in. Gravida justo maximus eleifend lorem aenean. Egestas tristique primis sit ridiculus enim vehicula."
                    ),
                    ChatMessage(
                        sender = "Riyas",
                        receiver = "OWNER",
                        content = "Volutpat finibus leo varius et conubia nibh. Imperdiet sit augue; magna finibus lacus velit ex. Montes dictum auctor condimentum rutrum lacinia amet suspendisse quam. Aliquam libero placerat turpis sit vulputate vitae. Netus montes sollicitudin ex primis aliquam facilisi? In rhoncus netus metus ad scelerisque lacinia"
                    ),
                    ChatMessage(
                        sender = "OWNER",
                        receiver = "Riyas",
                        content = "eget aptent cursus neque justo; molestie volutpat. Blandit sodales luctus vestibulum id in placerat"
                    ),
                )
            )
        }
    }

    fun onAction(action: ChattingScreenAction) {
        when(action) {
            is ChattingScreenAction.OnTypingTextChanged -> {
                _state.update {
                    it.copy(typedText = action.newText)
                }
            }
            is ChattingScreenAction.OnReplySelectionMessageChange -> {
                _state.update {
                    it.copy(replySelectionMessage = action.selectedMessage)
                }
            }

            ChattingScreenAction.OnReplySelectionMessageClose -> {
                _state.update {
                    it.copy(replySelectionMessage = null)
                }
            }
        }
    }
}

data class ChattingScreenState(
    val message: List<ChatMessage> = emptyList(),
    val typedText: String = "",
    val replySelectionMessage: ChatMessage? = null
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val receiver: String,
    val content: String,
)

sealed interface ChattingScreenAction {
    data class OnTypingTextChanged(val newText: String): ChattingScreenAction
    data class OnReplySelectionMessageChange(val selectedMessage: ChatMessage): ChattingScreenAction
    data object OnReplySelectionMessageClose: ChattingScreenAction
}