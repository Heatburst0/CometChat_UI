package com.example.cometchatUi

import com.example.cometchatUi.Model.ChatMessage

sealed class ChatItem {
    data class MessageItem(val message: ChatMessage) : ChatItem()
    data class DateHeader(val label: String) : ChatItem()
}
