package com.example.cometchatUi.Presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cometchatUi.MessageAction
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.layout.ContentScale

@Composable
fun MessageLongPressOverlay(
    message: String,
    mediaUrl: String? = null,
    mediaType: MediaType? = null, // <-- New param to identify media type
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit,
    onActionSelected: (MessageAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        keyboardController?.hide()
    }

    val backHandler = rememberUpdatedState(onDismiss)

    BackHandler(enabled = true) {
        backHandler.value()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 16.dp)
                .widthIn(max = 320.dp)
                .clickable(enabled = false) {}
        ) {

            // Media Preview (Image or Voice)
            if (!mediaUrl.isNullOrEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF6A1B9A),
                        modifier = Modifier
                            .width(220.dp)
                            .heightIn(min = 100.dp, max = 220.dp)
                    ) {
                        when (mediaType) {
                            MediaType.IMAGE -> {
                                AsyncImage(
                                    model = mediaUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                            MediaType.AUDIO -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Voice Message", color = Color.White)
                                }
                            }
                            MediaType.VIDEO -> {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(32.dp)
                                )
                            }
                            else -> Unit
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Text Message
            if (message.isNotBlank()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF6A1B9A),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = message,
                            color = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Emoji Bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.DarkGray)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.End)
            ) {
                listOf("👍", "❤️", "😂", "😍", "😢", "😡").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable { onEmojiSelected(emoji) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Popup
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .widthIn(min = 160.dp, max = 220.dp)
                    .background(Color.Transparent)
            ) {
                val scrollState = rememberScrollState()

                val baseActions = listOf(
                    MessageAction.Reply,
                    MessageAction.Share,
                    MessageAction.Copy,
                    MessageAction.Edit,
                    MessageAction.Info,
                    MessageAction.Delete,
                    MessageAction.Translate
                )

                // Filter actions for media messages
                val actions = if (mediaType != null) {
                    baseActions.filterNot {
                        it == MessageAction.Copy || it == MessageAction.Translate
                    }
                } else {
                    baseActions
                }

                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .verticalScroll(scrollState)
                        .padding(vertical = 4.dp)
                ) {
                    actions.forEach { action ->
                        DropdownMenuItem(
                            text = { Text(action.label) },
                            onClick = {
                                onActionSelected(action)
                                onDismiss()
                            },
                            leadingIcon = {
                                Icon(action.icon, contentDescription = action.label)
                            }
                        )
                    }
                }
            }
        }
    }
}


enum class MediaType {
    IMAGE, AUDIO, VIDEO
}
