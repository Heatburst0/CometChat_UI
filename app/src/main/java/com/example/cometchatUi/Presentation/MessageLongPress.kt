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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.cometchatUi.MessageAction
import com.example.cometchatUi.Presentation.ChatScreen.MessageActionsPopup

@Composable
fun MessageLongPressOverlay(
    message: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit,
    onActionSelected: (MessageAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dismiss keyboard immediately
    LaunchedEffect(Unit) {
        keyboardController?.hide()
    }

    val backHandler = rememberUpdatedState(onDismiss)

    // Dismiss on back press
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
            ) { onDismiss() } // Dismiss when clicking outside
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 16.dp)
                .clickable(enabled = false) {}
        ) {
            // Message
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6A1B9A),
                    modifier = Modifier.widthIn(max = 280.dp) // Limit width for visual appeal
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Emoji Bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.DarkGray)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.End) // 👉 ALIGN RIGHT
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
                    .widthIn(min = 180.dp, max = 250.dp)
                    .align(Alignment.End) // 👉 ALIGN RIGHT
                    .background(Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    val actions = listOf(
                        MessageAction.Reply,
                        MessageAction.Share,
                        MessageAction.Copy,
                        MessageAction.Edit,
                        MessageAction.Info,
                        MessageAction.Delete,
                        MessageAction.Translate
                    )

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




