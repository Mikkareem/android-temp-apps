package com.techullurgy.retest.voicerecorder

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun VoiceRecorderScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val voiceRecorder = remember { AndroidVoiceRecorder(context) }
    val audioPlayer = remember { ExoplayerAudioPlayer(context) }

    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }

    val snaps = remember { mutableStateListOf<Int>() }

    LaunchedEffect(isRecording) {
        val sizedList = SizedLinkList<Int>(40)
        if(isRecording) {
            voiceRecorder.listenForAmplitudeChanges(200).collectLatest {
                sizedList.add(it.coerceAtMost(10000))
                snaps.clear()
                snaps.addAll(sizedList.getData())
            }
        } else {
            while (snaps.isNotEmpty()) {
                snaps.removeAt(0)
                delay(100)
            }
        }
    }

    LaunchedEffect(isPlaying) {
        val sizedList = SizedLinkList<Int>(40)
        if(isPlaying) {
            audioPlayer.ampFlow.collectLatest {
                sizedList.add(it)
                snaps.clear()
                snaps.addAll(sizedList.getData())
            }
        } else {
            while (snaps.isNotEmpty()) {
                snaps.removeAt(0)
                delay(100)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                audioFile = File(context.cacheDir, "audio.mp3")
                voiceRecorder.record(audioFile!!)
                isRecording = true
            }
        ) {
            Text("Start Recording")
        }

        Button(
            onClick = {
                voiceRecorder.stop()
                isRecording = false
            }
        ) {
            Text("Stop Recording")
        }

//        Button(
//            onClick = {
//                audioPlayer.play("https://download.samplelib.com/mp3/sample-15s.mp3")
//            }
//        ) {
//            Text(
//                text = "Play"
//            )
//        }
//        Button(
//            onClick = {
//                audioPlayer.stop()
//            }
//        ) {
//            Text(
//                text = "Stop"
//            )
//        }

        audioFile?.let {
            Button(
                onClick = {
                    audioPlayer.playFile(it)
                    isPlaying = true
                }
            ) {
                Text(
                    text = "Play"
                )
            }
            Button(
                onClick = {
                    audioPlayer.stop()
                    isPlaying = false
                }
            ) {
                Text("Stop Playing")
            }
            Button(
                onClick = {
                    if(isPlaying) {
                        audioPlayer.pause()
                        isPlaying = false
                    } else {
                        audioPlayer.resume()
                        isPlaying = true
                    }
                }
            ) {
                Text("${if(isPlaying) "Pause" else "Resume"} Playing")
            }
        }

        Bars(snaps.toList())
    }
}

@Composable
private fun Bars(
    snaps: List<Int>,
    modifier: Modifier = Modifier
) {
    if(snaps.isNotEmpty()) {
        Canvas(
            modifier.fillMaxWidth().height(200.dp)
        ) {
            val eachRectWidth = size.width / snaps.size.coerceAtLeast(40)
            snaps.forEachIndexed { index, it ->
                val heightPercentage = lerp(0f, .5f, it/10000f)
                val height = size.height * heightPercentage
                val topLeft = center.copy(
                    x = index * eachRectWidth,
                    y = center.y - height/2f
                )
                val rectSize = Size(eachRectWidth, height)

                drawRect(
                    color = Color.Magenta,
                    topLeft = topLeft,
                    size = rectSize
                )
            }
        }
    }
}

private class SizedLinkList<T>(
    private val capacity: Int
) {
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var currentSize = 0

    fun add(data: T) {
        val node = Node(data)
        if(head == null && tail == null) {
            // First entry
            head = node
            tail = node
            currentSize++
        } else {
            tail!!.next = node
            node.prev = tail!!
            tail = node

            if(currentSize == capacity) {
                head = head!!.next
            } else {
                currentSize++
            }
        }
    }

    fun getData(): List<T> {
        val result = mutableListOf<T>()
        var current = head
        while(current != null) {
            result += current.value
            current = current.next
        }
        return result
    }

    private class Node<T> (
        val value: T,
        var next: Node<T>? = null,
        var prev: Node<T>? = null
    )
}