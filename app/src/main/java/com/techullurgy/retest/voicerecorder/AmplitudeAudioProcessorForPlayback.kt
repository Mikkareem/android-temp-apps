package com.techullurgy.retest.voicerecorder

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.max

@UnstableApi
class AmplitudeAudioProcessorForPlayback(
    private val onAmplitudeChanged: (Int) -> Unit
): BaseAudioProcessor() {
    private var maxAmplitude: Int = 0

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val inputPosition = inputBuffer.position()

        while (inputBuffer.hasRemaining() && !hasPendingOutput()) {
            val sample = abs(inputBuffer.getShort().toInt())
            maxAmplitude = max(maxAmplitude, sample)
        }

        inputBuffer.position(inputPosition)
        while (inputBuffer.hasRemaining() && !hasPendingOutput()) {
            replaceOutputBuffer(inputBuffer.remaining()).put(inputBuffer).flip()
        }
        onAmplitudeChanged(maxAmplitude)
        maxAmplitude = 0
        println("Running")
    }
}