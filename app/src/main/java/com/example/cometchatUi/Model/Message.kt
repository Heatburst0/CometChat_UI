package com.example.cometchatUi.Model

data class ChatMessage(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isSeen: Boolean = false,
    val status: String = "sent", // "sent", "delivered", "seen"
    val reactions: Map<String, List<String>> = emptyMap(),
    val edited: Boolean = false,
    val replyTo: RepliedMessage? = null,
    var deleted: Boolean = false,
    val messageType: String = "text", // "text", "audio", "image", "video", "file"
    val mediaUrl: String? = null
)

data class RepliedMessage(
    val messageId: String = "",
    val senderName: String = "",
    val message: String = ""
)

