package com.techullurgy.retest.workmanagertest.data.entities

data class ImageUpload(
    val id: Long,
    val uploadWorkId: String,
    val uploadStatus: ImageUploadStatus,
    val sourceContentUrl: String,
)