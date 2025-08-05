package com.example.cometchatUi.Presentation.ChatScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import coil.compose.rememberAsyncImagePainter
import com.example.cometchatUi.ChatActivity
import com.example.cometchatUi.ChatItem
import com.example.cometchatUi.ChatRepository
import com.example.cometchatUi.ChatRepository.sendMediaMessage
import com.example.cometchatUi.ChatRepository.updateReaction
import com.example.cometchatUi.ChatRepository.uploadMediaFile
import com.example.cometchatUi.MessageAction
import com.example.cometchatUi.Model.ChatActionOption
import com.example.cometchatUi.Model.ChatMessage
import com.example.cometchatUi.Model.RepliedMessage
import com.example.cometchatUi.Presentation.ChatAttachmentSheet
import com.example.cometchatUi.Presentation.DeleteConfirmationDialog
import com.example.cometchatUi.Presentation.MediaType
import com.example.cometchatUi.Presentation.MessageLongPressOverlay
import com.example.cometchatUi.Presentation.VoiceRecorderSheetContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import androidx.compose.foundation.lazy.items
import com.example.cometchatUi.CallActivity
import com.example.cometchatUi.Presentation.InitialsAvatar
import com.example.cometchatUi.UserInfoActivity
import java.util.UUID


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
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val showRecorderDialog = remember { mutableStateOf(false) }
    val duration = remember { mutableStateOf(0L) }
    var isRecording = remember { mutableStateOf(true) }
    val activity = context as ChatActivity
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val showSheet = remember { mutableStateOf(false) }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val showConfirmDialog = remember { mutableStateOf(false) }
    val stickers = remember { mutableStateListOf<String>() }
    val showStickers = remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Show confirmation dialog here
            selectedImageUri.value = uri
            showConfirmDialog.value = true
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // start recording
                showRecorderDialog.value = true
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Observe messages
    LaunchedEffect(chatId) {
        ChatRepository.observeMessages(chatId) {
            messages.clear()
            messages.addAll(it)
        }
    }

    LaunchedEffect(Unit) {
        val response = withContext(Dispatchers.IO) {
            val apiKey = "LIVDSRZULELA" // Free key
            val query = "stickers"
            val limit = 20
            val url = "https://g.tenor.com/v1/search?q=$query&key=$apiKey&limit=$limit"

            val result = URL(url).readText()
            JSONObject(result).getJSONArray("results").let { array ->
                for (i in 0 until array.length()) {
                    val media = array.getJSONObject(i).getJSONArray("media")
                    val gifUrl = media.getJSONObject(0)
                        .getJSONObject("tinygif") // or "nanogif", "mediumgif"
                        .getString("url")
                    stickers.add(gifUrl)
                }
            }
        }
    }

    // Mark as seen on first load
    LaunchedEffect(Unit) {
        ChatRepository.markMessagesAsSeen(chatId, currentUserId)
    }

    if (showRecorderDialog.value) {

        // Start recording + timer when dialog appears
        LaunchedEffect(key1 = showRecorderDialog.value) {
            if (showRecorderDialog.value) {
                isRecording.value = true
                duration.value = 0L
                activity.startRecording(context)

                while (showRecorderDialog.value) {
                    if (isRecording.value) {
                        delay(1000L)
                        duration.value += 1000L
                    } else {
                        delay(100L)
                    }
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                isRecording.value = false
                activity.stopRecording() // <- make sure recording is actually stopped on dismiss
                duration.value = 0L
                showRecorderDialog.value = false
            },
            sheetState = sheetState,
            dragHandle = null
        ) {
            VoiceRecorderSheetContent(
                duration = duration.value,
                isRecording = isRecording.value,

                onPause = {
                    isRecording.value = !isRecording.value
                },

                onDelete = {
                    isRecording.value = false
                    activity.stopRecording()
                    duration.value = 0L
                    showRecorderDialog.value = false
                },

                onStop = {
                    isRecording.value = false
                    activity.stopRecording()
                    duration.value = 0L
                },

                onSend = {
                    isRecording.value = false
                    val audioFilePath = activity.stopRecording()
                    duration.value = 0L

                    if (audioFilePath.toString().isNotEmpty()) {
                        val tempMessageId = UUID.randomUUID().toString()
                        val placeholderMessage = generateTempMessage(
                            messageType = "audio",
                            currentUserId = currentUserId,
                            receiverId = receiverId,
                            tempMessageId = tempMessageId
                            )

                        messages.add(placeholderMessage)
                        chatRepository.uploadMediaFile(
                            fileUri = Uri.fromFile(File(audioFilePath.toString())),
                            mediaTypeFolder = "audio",
                            onSuccess = { downloadUrl ->
                                chatRepository.sendMediaMessage(
                                    chatId = chatId,
                                    senderId = currentUserId,
                                    receiverId = receiverId,
                                    senderName = "You",
                                    receiverName = contactName,
                                    mediaUrl = downloadUrl,
                                    messageType = "audio",
                                    lastMessageLabel = "\uD83C\uDFA4 Voice Message",
                                    localMessageId = tempMessageId // send with reference to update UI if needed
                                )
                                val index = messages.indexOfFirst { it.messageId == tempMessageId }
                                if (index != -1) {
                                    messages.removeAt(index)
                                }

                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to upload audio", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    coroutineScope.launch {
                        sheetState.hide()
                        showRecorderDialog.value = false
                    }
                },

                onDismiss = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showRecorderDialog.value = false
                    }
                }
            )
        }
    }

    if (showConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog.value = false },
            title = { Text("Send Image?") },
            text = {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri.value),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog.value = false
                    selectedImageUri.value?.let { uri ->

                        val tempMessageId = UUID.randomUUID().toString()
                        val placeholderMessage = generateTempMessage(
                            messageType = "image",
                            currentUserId = currentUserId,
                            receiverId = receiverId,
                            tempMessageId = tempMessageId)

                        messages.add(placeholderMessage)

                        uploadMediaFile(
                            fileUri = uri,
                            mediaTypeFolder = "images",
                            onSuccess = { imageUrl ->
                                sendMediaMessage(
                                    chatId, currentUserId, receiverId,
                                    "You", contactName,
                                    mediaUrl = imageUrl,
                                    messageType = "image",
                                    lastMessageLabel = "\uD83D\uDDBC️ Photo",
                                    localMessageId = tempMessageId
                                )
                                val index = messages.indexOfFirst { it.messageId == tempMessageId }
                                if (index != -1) {
                                    messages.removeAt(index)
                                }
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                            }
                        )

                    }
                }) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    val attachmentOptions = listOf(
        ChatActionOption(Icons.Default.CameraAlt, "Camera") {
            // handleCamera()
        },
        ChatActionOption(Icons.Default.Image, "Attach Image") {
            imagePickerLauncher.launch("image/*")
        },
        ChatActionOption(Icons.Default.Videocam, "Attach Video") {
            // handleVideo()
        },
        ChatActionOption(Icons.Default.Mic, "Attach Audio") {
            // handleAudio()
        },
        ChatActionOption(Icons.Default.Description, "Attach Document") {
            // handleDocument()
        },
        ChatActionOption(Icons.Default.Edit, "Collaborative Document") {
            // handleCollaborativeDoc()
        },
        ChatActionOption(Icons.Default.BorderColor, "Collaborative Whiteboard") {
            // handleWhiteboard()
        },
        ChatActionOption(Icons.Default.Poll, "Poll") {
            // handlePoll()
        }
    )

    if (showSheet.value) {
        ChatAttachmentSheet(
            options = attachmentOptions,
            onDismissRequest = { showSheet.value = false }
        )
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.ime.asPaddingValues()), // handles keyboard overlap
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if(profileUrl.isNotEmpty()){
                            AsyncImage(
                                model = profileUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }else {
                            InitialsAvatar(name = contactName)
                        }
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
                    IconButton(onClick = {
                        val intent = Intent(context, CallActivity::class.java)
                        intent.putExtra("contactName", contactName)
                        intent.putExtra("currentUserId", currentUserId)
                        intent.putExtra("receiverId", receiverId)
                        intent.putExtra("chatId", chatId)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                    IconButton(onClick = { /* video call */ }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call")
                    }
                    IconButton(onClick = {
                        val intent = Intent(context, UserInfoActivity::class.java)
                        intent.putExtra("contactName", contactName)
                        intent.putExtra("currentUserId", currentUserId)
                        intent.putExtra("receiverId", receiverId)
                        intent.putExtra("chatId", chatId)
                        context.startActivity(intent)
                    }) {
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "Reply to • " + if (replyingTo.value?.senderId == currentUserId) "You" else contactName,
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Content box based on type
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2C2C2C), RoundedCornerShape(8.dp))
                                        .fillMaxWidth()
                                ) {
                                    when (replyingTo.value?.messageType) {
                                        "audio" -> {
                                            Row(
                                                modifier = Modifier
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Mic,
                                                    contentDescription = "Voice message",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Voice message",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        "image" -> {
                                            AsyncImage(
                                                model = replyingTo.value?.mediaUrl,
                                                contentDescription = "Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .height(60.dp)
                                                    .width(60.dp)
                                                    .padding(4.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            )
                                        }

                                        else -> {
                                            Text(
                                                modifier = Modifier.padding(10.dp),
                                                text = replyingTo.value?.message.orEmpty(),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
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
                    if (showStickers.value) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(if (isDark) Color(0xFF1C1C1C) else Color.White)
                                .padding(8.dp)
                        ) {
                            items(stickers) { stickerUrl ->
                                AsyncImage(
                                    model = stickerUrl,
                                    contentDescription = "Sticker",
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            val message = ChatMessage(
                                                senderId = currentUserId,
                                                receiverId = receiverId,
                                                message = "", // No text
                                                timestamp = System.currentTimeMillis(),
                                                messageType = "sticker",
                                                mediaUrl = stickerUrl.toString(), // Use resource ID or URL
                                                replyTo = replyingTo.value?.let {
                                                    RepliedMessage(
                                                        messageId = it.messageId,
                                                        senderName = if (it.senderId == currentUserId) "You" else contactName,
                                                        message = it.message,
                                                        type = it.messageType,
                                                        mediaUrl = it.mediaUrl
                                                    )
                                                }
                                            )
                                            ChatRepository.sendMessage(chatId, message, "You", contactName)
                                            replyingTo.value = null
                                            showStickers.value = false
                                        }
                                )
                            }
                        }
                    }



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
                    IconButton(onClick = {
                        showSheet.value= true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = {
                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                    }) {
                        Icon(Icons.Default.Mic, contentDescription = "Mic")
                    }
                    IconButton(onClick = {
                        showStickers.value = !showStickers.value
                    }) {
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
                                    newMessage = updatedMessage,
                                    senderName = "You",
                                    receiverName = contactName
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
                                            message = it.message,
                                            type = it.messageType,
                                            mediaUrl = it.mediaUrl
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
                LaunchedEffect(groupedItems.size) {
                    delay(100) // Allow layout/rendering
                    listState.animateScrollToItem(groupedItems.size.coerceAtLeast(0))
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
                if (showOptions.value && selectedMessage.value != null) {
                    val mediatype = if(selectedMessage.value!!.messageType == "image" || selectedMessage.value!!.messageType == "sticker") MediaType.IMAGE else if(selectedMessage.value!!.messageType == "audio") MediaType.AUDIO else MediaType.VIDEO
                    MessageLongPressOverlay(
                        message = selectedMessage.value?.message ?: "",
                        mediaType = if(selectedMessage.value?.message.isNullOrBlank()) mediatype else null,
                        mediaUrl = selectedMessage.value?.mediaUrl,
                        onDismiss = {
                            showOptions.value = false
                            if(!showDeleteConfirmation) selectedMessage.value = null
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
                                    selectedMessage.value = null
                                }

                                is MessageAction.Delete -> {
                                    showDeleteConfirmation = true
                                }

                                is MessageAction.Edit -> {
                                    messageText.value = selectedMessage.value?.message ?: ""
                                    messageBeingEdited.value = selectedMessage.value
                                    selectedMessage.value = null
                                }

                                is MessageAction.Reply -> {
                                    replyingTo.value = selectedMessage.value
                                    selectedMessage.value = null
                                }

                                is MessageAction.Share -> {
                                    Toast.makeText(context, "Share feature coming soon", Toast.LENGTH_SHORT).show()
                                    selectedMessage.value = null
                                }

                                is MessageAction.Info -> {
                                    Toast.makeText(context, "Info not yet implemented", Toast.LENGTH_SHORT).show()
                                    selectedMessage.value = null
                                }

                                is MessageAction.Translate -> {
                                    Toast.makeText(context, "Translate not yet implemented", Toast.LENGTH_SHORT).show()
                                    selectedMessage.value = null
                                }
                            }

                            showOptions.value = false

                        }
                    )
                }
                if (showDeleteConfirmation) {
                    val messageToDelete = selectedMessage.value!!
                    DeleteConfirmationDialog(
                        onConfirm = {
                            selectedMessage.let { message ->
                                ChatRepository.softDeleteMessage(
                                    chatId = chatId,
                                    messageId = messageToDelete.messageId,
                                    onSuccess = {
                                        selectedMessage.value = null
                                        showDeleteConfirmation = false
                                    },
                                    onFailure = {
                                        showDeleteConfirmation = false
                                        // Optional error feedback
                                    },
                                    senderName = "You",
                                    receiverName = contactName
                                )
                            }
                        },
                        onDismiss = { showDeleteConfirmation = false }
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

fun generateTempMessage(messageType : String, currentUserId: String, receiverId: String, tempMessageId : String) : ChatMessage{

    val timestamp = System.currentTimeMillis()
    return ChatMessage(
        messageId = tempMessageId,
        senderId = currentUserId,
        receiverId = receiverId,
        messageType = messageType,
        mediaUrl = "", // optional: you could store local file path here
        timestamp = timestamp,
        status = "uploading",
        isSeen = false,
        reactions = emptyMap(),
        edited = false,
        deleted = false
    )
}

