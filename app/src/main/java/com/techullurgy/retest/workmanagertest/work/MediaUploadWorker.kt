package com.techullurgy.retest.workmanagertest.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class MediaUploadWorker(
    private val appContext: Context,
    private val params: WorkerParameters
): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        println("Media Upload Work is running......")
        val data = inputData.getString("IMAGE_CONTENT_URI") ?: "Default Uri"
        delay(5000)
        println(data)
        println("Media Upload Work done.")
        return Result.success()
    }
}