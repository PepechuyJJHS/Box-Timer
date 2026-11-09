package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.BoxTimerScreen
import com.example.ui.theme.BoxTimerTheme
import com.example.viewmodel.BoxTimerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BoxTimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoxTimerTheme {
                BoxTimerScreen(viewModel = viewModel)
            }
        }
    }
}
