package com.example.cometchatUi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cometchatUi.Model.Contact
import com.example.cometchatUi.Presentation.ChatScreen.ChatScreen
import com.example.cometchatUi.ui.theme.CometChat_UITheme

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

        val currentUserId = "user_123" // This would be dynamic when you use Firebase Auth
        val receiverId = contact.name.lowercase().replace(" ", "_") // E.g., "John Doe" → "john_doe"
        val chatId = generateChatId(currentUserId, receiverId)
        setContent {
            CometChat_UITheme {
                ChatScreen(
                    contactName = contact.name,
                    profileUrl = contact.profileUrl,
                    isOnline = true,
                    lastSeen = "2 minutes ago",
                    onBackClick = { finish() },

                    // 👇 Required parameters
                    chatId = chatId,
                    chatRepository = ChatRepository,
                    currentUserId = currentUserId,
                    receiverId = receiverId
                )
            }
        }

    }

    private fun generateChatId(user1: String, user2: String): String {
        // Ensures chatId is the same for both users (lexicographically sorted)
        return listOf(user1, user2).sorted().joinToString("_")
    }
}