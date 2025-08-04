package com.example.cometchatUi.Model

data class ChatSummary(
    val chatId: String = "",
    val userId: String = "",              // other user's ID (receiver for sender, or vice versa)
    val userName: String = "",            // receiver's or sender's name
    val userProfileUrl: String = "",      // profile image of the other user
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val status: String = "",
    val messageId: String = ""
)

