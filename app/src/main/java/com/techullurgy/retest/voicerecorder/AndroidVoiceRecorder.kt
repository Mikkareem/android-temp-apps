package com.techullurgy.retest.voicerecorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

interface AudioRecorder {
    fun record(outputFile: File)
    fun stop()
    fun listenForAmplitudeChanges(interval: Long): Flow<Int>
}

class AndroidVoiceRecorder(
    private val context: Context
): AudioRecorder {
    private var recorder: MediaRecorder? = null

    private fun obtainMediaRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun listenForAmplitudeChanges(interval: Long): Flow<Int> {
        return channelFlow {
            launch(Dispatchers.IO) {
                while (recorder != null) {
                    send(recorder!!.maxAmplitude)
                    delay(interval)
                }
            }
            awaitClose {}
        }
    }

    override fun record(outputFile: File) {
        obtainMediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}