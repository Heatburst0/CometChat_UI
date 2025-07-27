package com.example.cometchat_ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cometchat_ui.Model.Contact
import com.example.cometchat_ui.Presentation.ChatScreen.ChatScreen
import com.example.cometchat_ui.Presentation.HomeScreen
import com.example.cometchat_ui.ui.theme.CometChat_UITheme

class ChatActivity : ComponentActivity() {
    private lateinit var contact: Contact
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        contact = Contact(
            name = intent.getStringExtra("contactName") ?: "",
            profileUrl = "",
            isOnline = true
        )
        setContent {
            CometChat_UITheme {
                ChatScreen(
                    contactName = contact.name,
                    profileUrl = contact.profileUrl,
                    isOnline = true,
                    lastSeen = "2 minutes ago",
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }
}