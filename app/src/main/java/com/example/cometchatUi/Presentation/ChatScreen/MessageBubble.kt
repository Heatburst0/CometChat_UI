package com.example.cometchatUi.Presentation.ChatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(isSender: Boolean, message: String, timestamp: Long, status: String) {
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

    Row(
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (isSender) Color(0xFF7C4DFF) else Color(0xFF444444),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Text(text = message, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = timeStr, fontSize = 10.sp, color = Color.LightGray)
                if (isSender) {
                    val icon = when (status) {
                        "sent" -> Icons.Default.Check
                        "delivered" -> Icons.Default.DoneAll
                        "seen" -> Icons.Default.DoneAll // Blue color
                        else -> null
                    }
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Status",
                            modifier = Modifier.size(16.dp).padding(start = 4.dp),
                            tint = if (status == "seen") Color.Cyan else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
