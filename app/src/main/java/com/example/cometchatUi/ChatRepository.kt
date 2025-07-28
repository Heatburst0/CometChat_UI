package com.example.cometchatUi

import com.example.cometchatUi.Model.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ChatRepository {
    private val db = FirebaseDatabase.getInstance().reference

    fun sendMessage(chatId: String, message: ChatMessage) {
        val msgRef = db.child("chats").child(chatId).child("messages").push()
        msgRef.setValue(message)
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
}