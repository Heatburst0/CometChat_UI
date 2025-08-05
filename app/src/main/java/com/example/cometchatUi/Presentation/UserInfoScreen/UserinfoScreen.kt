package com.example.cometchatUi.Presentation.UserInfoScreen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cometchatUi.Presentation.InitialsAvatar
import com.example.cometchatUi.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    userName: String,
    profileUrl: String?,
    isOnline: Boolean,
    onVoiceCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onBlockClick: () -> Unit,
    onDeleteChatClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Info") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = customColors.background
                )
            )

        },
        containerColor = customColors.background
    ) { paddingValues ->

        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Avatar
                if (!profileUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                } else {
                    InitialsAvatar(userName, modifier = Modifier.size(120.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name & Status
                Text(userName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Voice and Video Call Buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onVoiceCallClick,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Voice Call")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Voice")
                    }

                    OutlinedButton(
                        onClick = onVideoCallClick,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Video")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Block & Delete
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onBlockClick) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Block",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Block", color = Color.Red)
                    }

                    TextButton(onClick = onDeleteChatClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Chat",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Chat", color = Color.Red)
                    }
            }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun userInfoPreview(){
    UserInfoScreen(
        userName = "John Doe",
        profileUrl = null,
        isOnline = true,
        onVoiceCallClick = {},
        onVideoCallClick = {},
        onBlockClick = {},
        onDeleteChatClick = {},
        onBackClick = {}
    )
}
