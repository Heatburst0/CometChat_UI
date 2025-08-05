package com.example.cometchatUi

import android.net.Uri
import android.util.Log
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
                status = "sent",
                messageId = messageId
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
        status : String,
        messageId: String
    ) {
        val senderSummary = ChatSummary(
            chatId = "$senderId-$receiverId",
            userId = receiverId,
            userName = receiverName,
            lastMessage = lastMessage,
            timestamp = timestamp,
            status = status,
            messageId = messageId
        )

        val receiverSummary = ChatSummary(
            chatId = "$senderId-$receiverId",
            userId = senderId,
            userName = senderName,
            lastMessage = lastMessage,
            timestamp = timestamp,
            status = status,
            messageId = messageId
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
        newMessage: ChatMessage,
        senderName: String,
        receiverName: String
    ) {
        val messageRef = db.child("chats").child(chatId).child("messages").child(messageId)
        val updatedMap = mapOf(
            "message" to newMessage.message,
            "edited" to true
        )

        messageRef.updateChildren(updatedMap).addOnSuccessListener {
            db.child("chat_summaries").child(newMessage.senderId).child(newMessage.receiverId)
                .child("messageId").get()
                .addOnSuccessListener { snapshot ->
                    Log.d("Chatsummary", "messageId: ${snapshot.value}")
                    if (snapshot.value == messageId) {
                        updateChatSummaries(
                            senderId = newMessage.senderId,
                            receiverId = newMessage.receiverId,
                            senderName = senderName,
                            receiverName = receiverName,
                            lastMessage = newMessage.message,
                            timestamp = newMessage.timestamp,
                            status = "sent",
                            messageId = messageId
                        )
                    }
                }
        }
    }


    fun softDeleteMessage(
        chatId: String,
        messageId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {},
        senderName: String,
        receiverName: String
    ) {
        val messageRef = db.child("chats").child(chatId).child("messages").child(messageId)

        messageRef.get().addOnSuccessListener { snapshot ->
            val message = snapshot.getValue(ChatMessage::class.java)
            if (message != null) {
                val updateMap = mapOf(
                    "message" to "This message was deleted",
                    "messageType" to "text",
                    "deleted" to true
                )

                messageRef.updateChildren(updateMap).addOnSuccessListener {
                    // Delete media file if needed
                    if (message.messageType in listOf("image", "video", "audio")) {
                        message.mediaUrl?.let { url ->
                            FirebaseStorage.getInstance().getReferenceFromUrl(url).delete()
                        }
                    }

                    // Check if this message is the last one using ChatSummaries
                    db.child("chat_summaries").child(message.senderId).child(message.receiverId)
                        .child("messageId").get()
                        .addOnSuccessListener { summarySnap ->
                            if (summarySnap.value == messageId) {
                                updateChatSummaries(
                                    senderId = message.senderId,
                                    receiverId = message.receiverId,
                                    senderName = senderName, // Optional: pass real names if needed
                                    receiverName = receiverName,
                                    lastMessage = "This message was deleted",
                                    timestamp = message.timestamp,
                                    status = "deleted",
                                    messageId = messageId
                                )
                            }
                        }

                    onSuccess()
                }.addOnFailureListener { onFailure(it) }
            }
        }.addOnFailureListener { onFailure(it) }
    }



    fun uploadMediaFile(
        fileUri: Uri,
        mediaTypeFolder: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = System.currentTimeMillis().toString() + "_" + fileUri.lastPathSegment?.substringAfterLast("/")
        val mediaRef = storageRef.child("$mediaTypeFolder/$fileName")

        mediaRef.putFile(fileUri)
            .addOnSuccessListener {
                mediaRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun sendMediaMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        senderName: String,
        receiverName: String,
        mediaUrl: String,
        messageType: String,
        lastMessageLabel: String,
        localMessageId: String? = null // ✅ optional
    ) {
        val messageRef = db.child("chats").child(chatId).child("messages").push()
        val messageId = messageRef.key ?: return

        val messageMap = mapOf(
            "messageId" to messageId,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "messageType" to messageType,
            "mediaUrl" to mediaUrl,
            "timestamp" to ServerValue.TIMESTAMP,
            "status" to "pending",
            "isSeen" to false,
            "reactions" to mapOf<String, List<String>>(),
            "edited" to false,
            "deleted" to false
        )

        messageRef.setValue(messageMap).addOnSuccessListener {
            messageRef.child("status").setValue("sent")

            updateChatSummaries(
                senderId = senderId,
                receiverId = receiverId,
                senderName = senderName,
                receiverName = receiverName,
                lastMessage = lastMessageLabel,
                timestamp = System.currentTimeMillis(),
                status = "sent",
                messageId = messageId
            )
        }
    }

    fun deleteChat(currentUserId: String, receiverId: String){
        db.child("chat_summaries").child(currentUserId).child(receiverId).removeValue()
            .addOnSuccessListener {

            }
    }

    fun sendCallLogMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        senderName: String,
        receiverName: String,
        callType: String // e.g., "Outgoing Call", "Call Cancelled"
    ) {
        val messageRef = db.child("chats").child(chatId).child("messages").push()
        val messageId = messageRef.key ?: return

        val timestamp = System.currentTimeMillis()


        val callMessage = ChatMessage(
            messageId = messageId,
            senderId = senderId,
            receiverId = receiverId,
            message = callType, // "Outgoing Call" or "Call Cancelled"
            messageType = "call",
            timestamp = timestamp,
            status = "pending",
            receiverName = receiverName
        )

        messageRef.setValue(callMessage).addOnSuccessListener {
            messageRef.child("status").setValue("sent")

            updateChatSummaries(
                senderId = senderId,
                receiverId = receiverId,
                senderName = senderName,
                receiverName = receiverName,
                lastMessage = callType,
                timestamp = timestamp,
                status = "sent",
                messageId = messageId
            )
        }


        if(callType == "Outgoing Call") {
            val logId = db.child("call_logs").push().key ?: return

            val callLogEntry = callMessage.copy(messageId = logId)

            val updates = hashMapOf<String, Any>(
                "/call_logs/$senderId/$logId" to callLogEntry,
                "/call_logs/$receiverId/$logId" to callLogEntry
            )

            db.updateChildren(updates)
        }
    }

    fun fetchCallLogs(userId: String, onResult: (List<ChatMessage>) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("call_logs").child(userId)

        dbRef.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val callLogs = snapshot.children.mapNotNull {
                        it.getValue(ChatMessage::class.java)
                    }.sortedByDescending { it.timestamp }

                    onResult(callLogs)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("fetchCallLogs", "Error: ${error.message}")
                }
            })
    }





}