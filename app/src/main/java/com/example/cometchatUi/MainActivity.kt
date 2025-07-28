package com.example.cometchatUi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cometchatUi.Presentation.HomeScreen
import com.example.cometchatUi.ui.theme.CometChat_UITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CometChat_UITheme {
                HomeScreen(
                    onCreateClick = {
                        startActivity(Intent(this, NewChatActivity::class.java))
                    }
                )
            }
        }
    }
}

