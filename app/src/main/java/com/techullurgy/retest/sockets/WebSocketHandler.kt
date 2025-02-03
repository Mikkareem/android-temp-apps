package com.techullurgy.retest.sockets

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class WebSocketHandler(
    private val wsClient: HttpClient
) {
    private var scope: CoroutineScope? = null

    private var session: DefaultClientWebSocketSession? = null

    private val _messagesFlow = MutableStateFlow("")
    val messagesFlow = _messagesFlow.asStateFlow()

    fun connect(url: String) {
        scope?.cancel()
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope!!.launch {
            wsClient.webSocket(url) {
                session = this
                incoming
                    .receiveAsFlow()
                    .collectLatest {
                        if(it is Frame.Text) {
                            _messagesFlow.value = it.readText()
                        } else {
                            println(it)
                        }
                    }
            }
        }
    }

    fun sendMessage(msg: String = "Hi from client") {
        scope?.launch {
            session?.outgoing?.send(Frame.Text(msg))
        }
    }

    fun disconnect() {
        scope?.launch {
            session?.close()
            cancel()
            scope = null
            session = null
        }
    }
}