package com.example.cometchatUi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cometchatUi.Model.ChatMessage
import com.example.cometchatUi.Presentation.InitialsAvatar
import com.example.cometchatUi.ui.theme.LocalCustomColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallLogItem(
    message: ChatMessage,
    userName: String,
    profileUrl: String?,
    isOutgoing: Boolean,
    onCallClick: () -> Unit
) {

    val customColors = LocalCustomColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        if (!profileUrl.isNullOrEmpty()) {
            AsyncImage(
                model = profileUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            InitialsAvatar(userName)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = userName, fontWeight = FontWeight.Bold,
                color = customColors.basicText)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isOutgoing) Icons.Default.CallMade else Icons.Default.CallReceived,
                    contentDescription = null,
                    tint = if (isOutgoing) Color.Green else Color.Blue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.basicText
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        IconButton(onClick = onCallClick) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = Color(0xFF0A84FF)
            )
        }
    }
}


fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun calllogpreview(){
    CallLogItem(
        message = ChatMessage(
            messageId = "123",
            senderId = "user1",
            receiverId = "user2",
            message = "Outgoing Call",
        ),
        userName = "John Doe",
        profileUrl = null,
        isOutgoing = true,
        onCallClick = {}
    )
}
