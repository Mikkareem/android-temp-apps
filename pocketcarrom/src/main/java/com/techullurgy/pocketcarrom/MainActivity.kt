package com.techullurgy.pocketcarrom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.techullurgy.pocketcarrom.presentation.screens.GameTestScreen
import com.techullurgy.pocketcarrom.presentation.theme.PocketCarromTheme
import com.techullurgy.pocketcarrom.presentation.viewmodels.GameViewModel

const val BoardSizeInDp = 300
const val CoinSizeInDp = 20
const val StrikerSizeInDp = 30
const val HoleSizeInDp = 35
const val MaxVelocityDirectionAngle = 45f // [-45f..45f]

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel by viewModels<GameViewModel>()

            PocketCarromTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Background Image
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.g_bg),
                        contentDescription = "Game Background",
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        Modifier
                            .padding(innerPadding)
                    ) {
                        GameTestScreen(viewModel)
                    }
                }
            }
        }
    }
}