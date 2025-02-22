package com.techullurgy.chatsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.techullurgy.chatsample.ui.screens.ChattingScreen
import com.techullurgy.chatsample.ui.theme.RetestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(Modifier.fillMaxSize().background(Color.Red).padding(innerPadding)) {
                        ChattingScreen()
                    }
                }
            }
        }
    }
}