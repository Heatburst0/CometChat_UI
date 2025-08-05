package com.example.cometchatUi.Presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cometchatUi.Model.ChatSummary
import com.example.cometchatUi.ui.theme.LocalCustomColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun ChatListItem(summary: ChatSummary, userName: String, userImage: String) {

    val customColors = LocalCustomColors.current
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(summary.timestamp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (userImage.isEmpty()) {
            InitialsAvatar(name = userName)
        } else {
            AsyncImage(
                model = userImage,
                contentDescription = "image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = userName, fontWeight = FontWeight.Bold,
                color = customColors.basicText)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (summary.userId != "user_123") {
                    StatusIcon(status = summary.status)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${summary.lastMessage}", maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = customColors.basicText)
                } else {
                    Text(summary.lastMessage, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Text(text = timeStr, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun StatusIcon(status: String) {
    val icon = when (status) {
        "sent" -> Icons.Default.Check
        "delivered", "seen" -> Icons.Default.DoneAll
        else -> Icons.Default.Error // or your pending icon
    }
    val tint = when (status) {
        "seen" -> Color.Cyan
        else -> Color.Gray
    }
    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
}

@Composable
fun InitialsAvatar(name: String, modifier: Modifier = Modifier, size: Dp = 48.dp) {
    val initials = remember(name) {
        name.trim()
            .split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
    }

    // Create consistent color from initials hash
    val backgroundColor = remember(initials) {
        val colors = listOf(
            Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF5C6BC0),
            Color(0xFF29B6F6), Color(0xFF26A69A), Color(0xFFFF7043),
            Color(0xFFD4E157), Color(0xFF66BB6A)
        )
        colors[initials.hashCode().absoluteValue % colors.size]
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = size.value.times(0.4).sp
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ChatListItemPreview(){
    ChatListItem(

        summary = ChatSummary(
            chatId = "chat_id",
            userId = "user_id",
            userName = "John Doe",
        ),
        userName = "John Doe",
        userImage = ""
    )
}
