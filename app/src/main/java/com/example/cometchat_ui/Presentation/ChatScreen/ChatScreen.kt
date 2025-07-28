package com.example.cometchat_ui.Presentation.ChatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cometchat_ui.Model.Contact
import com.example.cometchat_ui.Presentation.ContactItem
import com.example.cometchat_ui.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactName: String,
    profileUrl: String,
    isOnline: Boolean,
    lastSeen: String?,
    onBackClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
    val textColor = if (isDark) Color.White else Color.Black
    val messageText = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
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
                            maxLines = 1, // 👈 limit to one line
                            overflow = TextOverflow.Ellipsis // 👈 show "..." if it's too long
                        )

                        Text(
                            text = if (isOnline) "Online" else "Last seen at $lastSeen",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
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

        // Chat messages placeholder
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
        ) {
            // Your message list will go here
            Text("Chat messages go here", modifier = Modifier.align(Alignment.Center))
        }

        // Bottom input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
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
                shape = RoundedCornerShape(24.dp)
            )
        }
        Spacer(
            modifier = Modifier
                .height(5.dp)
                .fillMaxWidth()
        )
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .padding(horizontal = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
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
                onClick = { /* send message */ },
                enabled = messageText.value.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (messageText.value.isNotBlank()) Color(0xFF2196F3) else Color.Gray
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview(){
    ChatScreen(
        contactName = "John Doe",
        profileUrl = "https://example.com/profile.jpg",
        isOnline = true,
        lastSeen = "2 minutes ago",
        onBackClick = {}
    )
}
