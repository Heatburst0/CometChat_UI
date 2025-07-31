package com.example.cometchatUi

import android.net.Uri
import android.widget.Toast
import com.example.cometchatUi.Model.ChatMessage
import com.example.cometchatUi.Model.ChatSummary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

object ChatRepository {
    private val db = FirebaseDatabase.getInstance().reference

    fun sendMessage(
        chatId: String,
        message: ChatMessage,
        senderName: String,
        receiverName: String
    ) {
        val msgRef = db.child("chats").child(chatId).child("messages").push()
        val messageId = msgRef.key ?: return

        // Add messageId and mark status as pending
        val messageWithId = message.copy(
            messageId = messageId,
            status = "pending"
        )

        msgRef.setValue(messageWithId).addOnSuccessListener {
            // Update status to "sent"
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

    fun updateReaction(
        messageId: String,
        chatRoomId: String,
        currentUserId: String,
        emoji: String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("chats/$chatRoomId/messages/$messageId")

        dbRef.get().addOnSuccessListener { snapshot ->
            val existingReactions = snapshot.child("reactions").getValue(object :
                GenericTypeIndicator<Map<String, List<String>>>() {}) ?: mapOf()

            val updatedReactions = existingReactions.toMutableMap()
            val users = updatedReactions[emoji]?.toMutableList() ?: mutableListOf()

            if (currentUserId !in users) {
                users.add(currentUserId)
                updatedReactions[emoji] = users
            } else {
                users.remove(currentUserId)
                if (users.isEmpty()) {
                    updatedReactions.remove(emoji)
                } else {
                    updatedReactions[emoji] = users
                }
            }

            dbRef.child("reactions").setValue(updatedReactions)
        }
    }

    fun editMessage(
        chatId: String,
        messageId: String,
        newMessage: ChatMessage
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("chats/$chatId/messages/$messageId")

        val updatedMap = mapOf(
            "message" to newMessage.message,
            "edited" to true // Add an 'edited' flag to show message was updated
        )

        dbRef.updateChildren(updatedMap)
            .addOnSuccessListener {  }
            .addOnFailureListener {  }
    }

    fun softDeleteMessage(
        chatId: String,
        messageId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("chats/$chatId/messages/$messageId")

        val softDeleteMap = mapOf(
            "message" to "This message was deleted",
            "deleted" to true
        )

        dbRef.updateChildren(softDeleteMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun uploadAudioFile(filePath: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileUri = Uri.fromFile(File(filePath))
        val audioRef = storageRef.child("voice_notes/${fileUri.lastPathSegment}")

        audioRef.putFile(fileUri)
            .addOnSuccessListener {
                audioRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun sendAudioMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        senderName: String,
        receiverName: String,
        audioUrl: String
    ) {
        val messageRef = db.child("chats").child(chatId).child("messages").push()
        val messageId = messageRef.key ?: return

        val messageMap = mapOf(
            "messageId" to messageId,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "messageType" to "audio",
            "mediaUrl" to audioUrl,
            "timestamp" to ServerValue.TIMESTAMP,
            "status" to "pending",
            "isSeen" to false,
            "reactions" to mapOf<String, List<String>>(),
            "edited" to false,
            "deleted" to false
        )

        messageRef.setValue(messageMap).addOnSuccessListener {
            messageRef.child("status").setValue("sent")

            // Update chat summaries
            updateChatSummaries(
                senderId = senderId,
                receiverId = receiverId,
                senderName = senderName,
                receiverName = receiverName,
                lastMessage = "\uD83C\uDFA4 Voice Message",
                timestamp = System.currentTimeMillis(),
                status = "sent"
            )
        }
    }



}