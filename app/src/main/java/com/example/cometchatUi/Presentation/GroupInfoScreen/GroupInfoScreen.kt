package com.example.cometchatUi.Presentation.GroupInfoScreen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cometchatUi.Presentation.InitialsAvatar
import com.example.cometchatUi.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupInfoScreen(
    groupName: String,
    memberCount: Int,
    profileUrl: String?,
    onViewMembersClick: () -> Unit,
    onAddMembersClick: () -> Unit,
    onBannedMembersClick: () -> Unit,
    onLeaveGroupClick: () -> Unit,
    onDeleteGroupClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val customColors = LocalCustomColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Info") },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Group Image
            if (!profileUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileUrl,
                    contentDescription = "Group Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            } else {
                // fallback avatar (initials)
                InitialsAvatar(groupName, modifier = Modifier.size(120.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group name and member count
            Text(groupName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$memberCount Members",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Member actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GroupActionButton("View Members", Icons.Default.Group, onClick = onViewMembersClick)
                GroupActionButton("Add Members", Icons.Default.GroupAdd, onClick = onAddMembersClick)
                GroupActionButton("Banned Members", Icons.Default.Block, onClick = onBannedMembersClick)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Leave and Delete buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                TextButton(onClick = onLeaveGroupClick) {
                    Icon(Icons.Default.Block, contentDescription = "Leave", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Leave", color = Color.Red)
                }

                TextButton(onClick = onDeleteGroupClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete and Exit", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete and Exit?", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun GroupActionButton(title: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.DarkGray)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, tint = Color(0xFF9B59B6)) // Purple-ish
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF9B59B6),
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun groupinfopreview(){
    GroupInfoScreen(
        groupName = "",
        memberCount = 0,
        profileUrl = null,
        onViewMembersClick = {},
        onAddMembersClick = {},
        onBannedMembersClick = {},
        onLeaveGroupClick = {},
        onDeleteGroupClick = {},
        onBackClick = {}
    )
}

