package com.techullurgy.retest.workmanagertest.ui

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.impl.awaitWithin
import androidx.work.workDataOf
import com.techullurgy.retest.workmanagertest.work.MediaUploadWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MediaUploadScreen() {

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    var statusText by remember { mutableStateOf("") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let {  uri ->
            val workManager = WorkManager.getInstance(context)

            val workRequest = OneTimeWorkRequestBuilder<MediaUploadWorker>()
                .setInitialDelay(Duration.ofSeconds(10))
                .setInputData(
                    workDataOf(
                        "IMAGE_CONTENT_URI" to uri.toString()
                    )
                )
                .build()

            workManager.getWorkInfoByIdFlow(workRequest.id)
                .onEach { info ->
                    if (info != null) {
                        when(info.state) {
                            WorkInfo.State.ENQUEUED -> {
                                statusText = "Work is enqueued"
                            }

                            WorkInfo.State.RUNNING -> {
                                statusText = "Work is running"
                            }

                            WorkInfo.State.SUCCEEDED -> {
                                statusText = "Work is Succeeded"
                            }

                            WorkInfo.State.FAILED -> {
                                statusText = "Work is failed"
                            }

                            WorkInfo.State.BLOCKED -> {
                                statusText = "Work is Blocked"
                            }

                            WorkInfo.State.CANCELLED -> {
                                statusText = "Work is cancelled"
                            }
                        }
                    }
                }
                .launchIn(coroutineScope)

            workManager.enqueue(workRequest)

            coroutineScope.launch {
                val newRequest = OneTimeWorkRequestBuilder<MediaUploadWorker>()
                    .setInputData(
                        workDataOf(
                            "IMAGE_CONTENT_URI" to "New Request Changed"
                        )
                    )
                    .setId(workRequest.id)
                    .build()

                delay(3000)
                val updateResult = workManager.updateWork(newRequest).get()

                when(updateResult) {
                    WorkManager.UpdateResult.NOT_APPLIED -> {
                        println("Not Applied now")
                    }
                    WorkManager.UpdateResult.APPLIED_IMMEDIATELY -> {
                        println("Applied Immediately")
                    }
                    WorkManager.UpdateResult.APPLIED_FOR_NEXT_RUN -> {
                        println("Applied for next run")
                    }
                    else -> {
                        println("UpdateResult Null")
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statusText
            )
            Button(
                onClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text("Upload image")
            }
        }
    }
}