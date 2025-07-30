package com.example.cometchatUi.Presentation.ChatScreen

import android.R.attr.text
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cometchatUi.ChatItem
import com.example.cometchatUi.ChatRepository
import com.example.cometchatUi.ChatRepository.updateReaction
import com.example.cometchatUi.MessageAction
import com.example.cometchatUi.Model.ChatMessage
import com.example.cometchatUi.Model.RepliedMessage
import com.example.cometchatUi.Presentation.MessageLongPressOverlay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactName: String,
    profileUrl: String,
    isOnline: Boolean,
    lastSeen: String?,
    onBackClick: () -> Unit,
    currentUserId: String,
    receiverId: String,
    chatRepository: ChatRepository,
    chatId: String
) {

    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
    val textColor = if (isDark) Color.White else Color.Black
    val messageText = remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val messageBeingEdited = remember { mutableStateOf<ChatMessage?>(null) }
    val replyingTo = remember { mutableStateOf<ChatMessage?>(null) }
    val showOptions = remember { mutableStateOf(false) }
    val selectedMessage = remember { mutableStateOf<ChatMessage?>(null) }
    // Observe messages
    LaunchedEffect(chatId) {
        ChatRepository.observeMessages(chatId) {
            messages.clear()
            messages.addAll(it)
        }
    }

    // Mark as seen on first load
    LaunchedEffect(Unit) {
        ChatRepository.markMessagesAsSeen(chatId, currentUserId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.ime.asPaddingValues()), // handles keyboard overlap
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = profileUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = contactName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isOnline) "Online" else "Last seen at $lastSeen",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* call */ }) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                    IconButton(onClick = { /* video call */ }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call")
                    }
                    IconButton(onClick = { /* info */ }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(if (isDark) Color(0xFF1C1C1C) else Color.White)
            ) {
                if (replyingTo.value != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) Color(0xFF1C1C1C) else Color.White)
                            .padding(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)
                                .padding(start = 8.dp)) {
                                Text(
                                    text = "Reply to • " + if (replyingTo.value?.senderId == currentUserId) "You" else contactName,
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2C2C2C), RoundedCornerShape(8.dp))
                                        .fillMaxWidth()
                                ){
                                    Text(
                                        modifier = Modifier.padding(10.dp),
                                        text = replyingTo.value?.message.orEmpty(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                            }
                            IconButton(onClick = { replyingTo.value = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel Reply")
                            }
                        }
                    }
                    HorizontalDivider()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText.value,
                        onValueChange = { messageText.value = it },
                        placeholder = { Text("Message") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            disabledContainerColor = containerColor,
                            errorContainerColor = containerColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            disabledTextColor = textColor
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* add more */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = { /* mic */ }) {
                        Icon(Icons.Default.Mic, contentDescription = "Mic")
                    }
                    IconButton(onClick = { /* sticker */ }) {
                        Icon(Icons.Outlined.EmojiEmotions, contentDescription = "Stickers")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            val editingMessage = messageBeingEdited.value
                            val trimmedText = messageText.value.trim()
                            val replyToMessage = replyingTo.value

                            if (editingMessage != null) {
                                val updatedMessage = editingMessage.copy(
                                    message = trimmedText,
                                    timestamp = System.currentTimeMillis()
                                )
                                ChatRepository.editMessage(
                                    chatId = chatId,
                                    messageId = editingMessage.messageId,
                                    newMessage = updatedMessage
                                )
                                messageBeingEdited.value = null
                            } else if (trimmedText.isNotBlank()) {
                                val message = ChatMessage(
                                    senderId = currentUserId,
                                    receiverId = receiverId,
                                    message = trimmedText,
                                    timestamp = System.currentTimeMillis(),
                                    replyTo = replyToMessage?.let {
                                        RepliedMessage(
                                            messageId = it.messageId,
                                            senderName = if (it.senderId == currentUserId) "You" else contactName,
                                            message = it.message
                                        )
                                    }
                                )
                                ChatRepository.sendMessage(
                                    chatId = chatId,
                                    message = message,
                                    senderName = "You",
                                    receiverName = contactName
                                )
                            }
                            replyingTo.value = null
                            messageText.value = ""
                        }
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (messageText.value.isNotBlank()) Color(0xFF2196F3) else Color.Gray
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(if (isDark) Color(0xFF1A1A1A) else Color(0xFFF5F5F5))
            ) {
                val groupedItems by remember {
                    derivedStateOf { groupMessagesWithDateHeaders(messages.toList()) }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    reverseLayout = false,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(groupedItems.size) { index ->
                        when (val item = groupedItems[index]) {
                            is ChatItem.DateHeader -> {
                                DateDivider(label = item.label)
                            }
                            is ChatItem.MessageItem -> {
                                val msg = item.message
                                MessageBubble(
                                    modifier = Modifier, // Add animation or transition if needed
                                    isSender = msg.senderId == currentUserId,
                                    message = msg.message,
                                    timestamp = msg.timestamp,
                                    status = msg.status,
                                    reactions = msg.reactions,
                                    onReact = { selectedEmoji ->
                                        updateReaction(
                                            messageId = msg.messageId,
                                            chatRoomId = chatId,
                                            currentUserId = currentUserId,
                                            emoji = selectedEmoji
                                        )
                                    },
                                    onLongPress = {
                                        selectedMessage.value = msg
                                        showOptions.value = true
                                    },
                                    chatMessage = msg
                                )
                            }
                        }
                    }
                }
                LaunchedEffect(messages.size) {
                    listState.animateScrollToItem(messages.size.coerceAtLeast(0))
                }
                if (showOptions.value && selectedMessage.value != null) {
                    MessageLongPressOverlay(
                        message = selectedMessage.value!!.message,
                        onDismiss = {
                            showOptions.value = false
                            selectedMessage.value = null
                        },
                        onEmojiSelected = { emoji ->
                            selectedMessage.value?.let {
                                updateReaction(
                                    messageId = it.messageId,
                                    chatRoomId = chatId,
                                    currentUserId = currentUserId,
                                    emoji = emoji
                                )
                            }
                            showOptions.value = false
                            selectedMessage.value = null
                        },
                        onActionSelected = { action ->
                            when (action) {
                                is MessageAction.Copy -> {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("message", selectedMessage.value!!.message))
                                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                                }

                                is MessageAction.Delete -> {
                                    // ChatRepository.deleteMessage(chatId, selectedMessage.value!!.messageId)
                                    Toast.makeText(context, "Delete not yet implemented", Toast.LENGTH_SHORT).show()
                                }

                                is MessageAction.Edit -> {
                                    messageText.value = selectedMessage.value?.message ?: ""
                                    messageBeingEdited.value = selectedMessage.value // Store the message object
                                }

                                is MessageAction.Reply -> {
                                    replyingTo.value = selectedMessage.value
                                    selectedMessage.value = null
                                }

                                is MessageAction.Share -> {
                                    Toast.makeText(context, "Share feature coming soon", Toast.LENGTH_SHORT).show()
                                }

                                is MessageAction.Info -> {
                                    Toast.makeText(context, "Info not yet implemented", Toast.LENGTH_SHORT).show()
                                }

                                is MessageAction.Translate -> {
                                    Toast.makeText(context, "Translate not yet implemented", Toast.LENGTH_SHORT).show()
                                }
                            }

                            showOptions.value = false
                            selectedMessage.value = null
                        }
                    )
                }

            }
        }

    )
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview(){
    ChatScreen(
        contactName = "John Doe",
        profileUrl = "https://example.com/profile.jpg",
        isOnline = true,
        lastSeen = "2 minutes ago",
        onBackClick = {},
        currentUserId = "user_id",
        receiverId = "receiver_id",
        chatRepository = ChatRepository,
        chatId = "chat_id"
    )
}

fun groupMessagesWithDateHeaders(messages: List<ChatMessage>): List<ChatItem> {
    val result = mutableListOf<ChatItem>()
    var lastHeader: String? = null

    for (message in messages.sortedBy { it.timestamp }) {
        val dateLabel = formatDateLabel(message.timestamp)

        if (dateLabel != lastHeader) {
            result.add(ChatItem.DateHeader(dateLabel))
            lastHeader = dateLabel
        }
        result.add(ChatItem.MessageItem(message))
    }

    return result
}


@Composable
fun DateDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            color = Color.LightGray.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
fun formatDateLabel(timestamp: Long): String {
    val now = Calendar.getInstance()
    val msgDate = Calendar.getInstance().apply { timeInMillis = timestamp }

    return when {
        isSameDay(now, msgDate) -> "Today"
        isYesterday(now, msgDate) -> "Yesterday"
        else -> {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

fun isYesterday(now: Calendar, date: Calendar): Boolean {
    val yesterday = Calendar.getInstance().apply {
        timeInMillis = now.timeInMillis
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return isSameDay(yesterday, date)
}




