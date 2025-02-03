package com.techullurgy.retest.voicerecorder

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi
class ExoplayerAudioPlayer(
    private val context: Context
): AudioPlayer {
    private val audioProcessor = AmplitudeAudioProcessorForPlayback {
        coroutineScope?.launch {
            _processChannel.send(it)
        }
    }

    private var exoPlayer: Player? = null

    private val _processChannel = Channel<Int>()
    val ampFlow = _processChannel.receiveAsFlow()

    private var coroutineScope: CoroutineScope? = null
        get() = field ?: CoroutineScope(SupervisorJob())

    private fun initPlayer(): Player = ExoPlayer.Builder(
        context,
        object : DefaultRenderersFactory(context) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): AudioSink? {
                return DefaultAudioSink.Builder(context)
                    .setEnableFloatOutput(enableFloatOutput)
                    .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                    .setAudioProcessors(arrayOf(audioProcessor))
                    .build()
            }
        }
    )
        .build()
        .also {
            exoPlayer = it
        }

    private fun playItem(item: MediaItem) {
        initPlayer()
            .apply {
                setMediaItem(item)
                prepare()
                playWhenReady = true
            }
    }

    override fun playFile(file: File) {
        val mediaItem = MediaItem.fromUri(file.toUri())
        playItem(mediaItem)
    }

    override fun pause() {
        exoPlayer?.pause()
    }

    override fun resume() {
        exoPlayer?.play()
    }

    override fun stop() {
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
        coroutineScope?.cancel()
        coroutineScope = null
    }
}