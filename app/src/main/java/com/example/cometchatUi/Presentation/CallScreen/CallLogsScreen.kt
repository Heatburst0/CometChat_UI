package com.example.cometchatUi.Presentation.CallScreen

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.cometchatUi.ChatRepository.fetchCallLogs
import com.example.cometchatUi.Model.ChatMessage
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.cometchatUi.CallActivity
import com.example.cometchatUi.CallLogItem
import com.example.cometchatUi.ChatActivity

@Composable
fun CallLogsScreen(
    currentUserId: String,
    onStartCall: (receiverId: String) -> Unit
) {
    var callLogs by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(currentUserId) {
        fetchCallLogs(currentUserId) { logs ->
            callLogs = logs
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(callLogs) { message ->
            val name = message.receiverName
            val profileUrl =""
            val chatId = generateChatId(currentUserId, message.receiverId)
            CallLogItem(
                message = message,
                userName = name,
                profileUrl = profileUrl,
                isOutgoing = message.senderId == currentUserId,
                onCallClick = {
                    val intent = Intent(context, CallActivity::class.java)
                    intent.putExtra("contactName", name)
                    intent.putExtra("chatId", chatId)
                    intent.putExtra("receiverId", message.receiverId)
                    intent.putExtra("currentUserId", message.senderId)
                    context.startActivity(intent)

                }
            )
        }
    }
}

private fun generateChatId(user1: String, user2: String): String {
    return listOf(user1, user2).sorted().joinToString("_")
}
