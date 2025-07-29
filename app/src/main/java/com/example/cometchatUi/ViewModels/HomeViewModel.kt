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
//    fun refreshSingleChatSummary(currentUserId: String, otherUserId: String) {
//        val summaryRef = FirebaseDatabase.getInstance()
//            .getReference("chat_summaries")
//            .child(currentUserId)
//            .child(otherUserId)
//
//        summaryRef.get().addOnSuccessListener { snapshot ->
//            val updatedSummary = snapshot.getValue(ChatSummary::class.java)
//            updatedSummary?.let {
//                val index = chatSummaries.indexOfFirst { it.userId == otherUserId }
//                if (index != -1) {
//                    chatSummaries[index] = it
//                    chatSummaries = chatSummaries.toList() // trigger Compose update
//                }
//            }
//        }
//    }

}
