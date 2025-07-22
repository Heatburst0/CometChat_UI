package com.example.cometchat_ui.Model

data class Chat(
    val name: String,
    val lastMessage: String,
    val time: String,
    val imageUrl: String,
    val isRead: Boolean
)
