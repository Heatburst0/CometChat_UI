package com.example.cometchatUi.Model

data class ChatMessage(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isSeen: Boolean = false,
    val status: String = "sent", // "sent", "delivered", "seen"
    val reactions: Map<String, List<String>> = emptyMap() // emoji -> userIds
)
