package com.techullurgy.retest.workmanagertest.usecases

import android.content.Context
import android.net.Uri

class ImageUploadUsecase(
    private val context: Context
) {
    operator fun invoke(uri: Uri) {
        // Store necessary states in DB
        // Call WorkManager.enqueue

    }
}