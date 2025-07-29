package com.example.cometchatUi

import com.example.cometchatUi.Model.ChatMessage
import com.example.cometchatUi.Model.ChatSummary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ChatRepository {
    private val db = FirebaseDatabase.getInstance().reference

    fun sendMessage(
        chatId: String,
        message: ChatMessage,
        senderName: String,
        receiverName: String
    ) {
        val msgRef = db.child("chats").child(chatId).child("messages").push()
        val pendingMessage = message.copy(status = "pending")

        msgRef.setValue(pendingMessage).addOnSuccessListener {
            // Update status
            msgRef.child("status").setValue("sent")

            // Update chat summaries for both users
            updateChatSummaries(
                senderId = message.senderId,
                receiverId = message.receiverId,
                senderName = senderName,
                receiverName = receiverName,
                lastMessage = message.message,
                timestamp = message.timestamp,
                status = "sent"
            )
        }
    }

    private fun updateChatSummaries(
        senderId: String,
        receiverId: String,
        senderName: String,
        receiverName: String,
        lastMessage: String,
        timestamp: Long,
        status : String
    ) {
        val senderSummary = ChatSummary(
            chatId = "$senderId-$receiverId",
            userId = receiverId,
            userName = receiverName,
            lastMessage = lastMessage,
            timestamp = timestamp,
            status = status
        )

        val receiverSummary = ChatSummary(
            chatId = "$senderId-$receiverId",
            userId = senderId,
            userName = senderName,
            lastMessage = lastMessage,
            timestamp = timestamp,
            status = status
        )

        db.child("chat_summaries").child(senderId).child(receiverId).setValue(senderSummary)
        db.child("chat_summaries").child(receiverId).child(senderId).setValue(receiverSummary)
    }


    fun observeMessages(chatId: String, onMessagesUpdated: (List<ChatMessage>) -> Unit) {
        db.child("chats").child(chatId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull {
                        it.getValue(ChatMessage::class.java)
                    }
                    onMessagesUpdated(messages)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun markMessagesAsSeen(chatId: String, userId: String) {
        db.child("chats").child(chatId).child("messages")
            .get().addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    val message = it.getValue(ChatMessage::class.java)
                    if (message?.receiverId == userId && message.status != "seen") {
                        it.ref.child("status").setValue("seen")
                    }
                }
            }
    }
    fun getChatSummaries(userId: String, onResult: (List<ChatSummary>) -> Unit) {
        db.child("chat_summaries").child(userId)
            .orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chats = mutableListOf<ChatSummary>()
                    for (chatSnap in snapshot.children) {
                        val summary = chatSnap.getValue(ChatSummary::class.java)
                        if (summary != null) {
                            chats.add(summary)
                        }
                    }
                    onResult(chats.reversed())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}