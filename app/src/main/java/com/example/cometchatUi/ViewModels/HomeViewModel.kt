package com.example.cometchatUi.ViewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.cometchatUi.ChatRepository
import com.example.cometchatUi.Model.ChatSummary
import com.google.firebase.database.FirebaseDatabase

class HomeViewModel : ViewModel() {
    private val _chatSummaries = mutableStateListOf<ChatSummary>()
    val chatSummaries: List<ChatSummary> get() = _chatSummaries

    fun loadChatSummaries(currentUserId: String) {
        ChatRepository.getChatSummaries(currentUserId) { summaries ->
            _chatSummaries.clear()
            _chatSummaries.addAll(summaries)
        }
    }
    fun deleteChat(currentUserId: String, contactName: String){

        val receiverId = contactName.lowercase().replace(" ", "_")
        ChatRepository.deleteChat(currentUserId, receiverId)

        loadChatSummaries(currentUserId)
    }

}
