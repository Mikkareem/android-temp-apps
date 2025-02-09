package com.techullurgy.openglsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.techullurgy.openglsample.components.CameraGears
import com.techullurgy.openglsample.components.ControlPanel
import com.techullurgy.openglsample.ui.theme.RetestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(innerPadding)
                    ) {
                        var surfaceView by remember {
                            mutableStateOf<OpenGLSurfaceView?>(null)
                        }

                        AndroidView(
                            factory = {
                                OpenGLSurfaceView(context = it).also { view ->
                                    surfaceView = view
                                }
                            },
                            onReset = {
                                surfaceView = it
                            },
                            onRelease = {
                                surfaceView = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(32.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CameraGears(view = surfaceView)
                            ControlPanel(view = surfaceView)
                        }
                    }
                }
            }
        }
    }
}