package com.example.cometchatUi.Presentation.ChatScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    isSender: Boolean,
    message: String,
    timestamp: Long,
    status: String,
    reactions: Map<String, List<String>>,
    onReact: (emoji: String) -> Unit,
    onLongPress: () -> Unit,
    edited : Boolean// <- for triggering popup
) {
    val showEmojiBar = remember { mutableStateOf(false) }
    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { showEmojiBar.value = false },
                onLongClick = {
                    onLongPress() // Show popup
                }
            ),
        horizontalAlignment = if (isSender) Alignment.End else Alignment.Start
    ) {
        val context = LocalContext.current
        if (edited) {
            Text(
                text = "(edited)",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        if (showEmojiBar.value) {
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .background(Color(0xFF2E2E2E), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("👍", "❤️", "😂", "😍", "😢", "😡").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable {
                                onReact(emoji)
                                showEmojiBar.value = false
                            }
                    )
                }
            }
        }

        Box {
            Column(
                modifier = Modifier
                    .background(
                        color = if (isSender) Color(0xFF7C4DFF) else Color(0xFF444444),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = message, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = formattedTime, fontSize = 10.sp, color = Color.LightGray)
                    if (isSender) {
                        val icon = when (status) {
                            "sent" -> Icons.Default.Check
                            "delivered", "seen" -> Icons.Default.DoneAll
                            "pending" -> Icons.Outlined.AccessTime
                            else -> null
                        }
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "Status",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp),
                                tint = if (status == "seen") Color.Cyan else Color.Gray
                            )
                        }
                    }
                }
            }

            // 🔥 Show reaction counts below the message
            if (reactions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = 20.dp)
                        .background(Color(0xFF2C2C2C), RoundedCornerShape(16.dp))
                        .clickable{
                            Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    reactions.forEach { (emoji, users) ->
                        if (users.isNotEmpty()) {
                            Text(
                                text = "$emoji ${users.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun MessageBubblePreview(){
    MessageBubble(
        modifier = Modifier,
        isSender = true,
        message = "Hello, how are you?",
        timestamp = System.currentTimeMillis(),
        status = "pending",
        reactions = mapOf(),
        onReact = {},
        onLongPress = {},
        true
    )
}
