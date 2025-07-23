package com.example.cometchat_ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cometchat_ui.Presentation.NewChatScreen.NewChatScreen
import com.example.cometchat_ui.ui.theme.CometChat_UITheme

class NewChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CometChat_UITheme {
                NewChatScreen(
                    onBackClick = {
                        Log.d("NewChatScreen", "Back Clicked")
                        finish()
                    }
                )
            }
        }
    }
}