package com.example.cometchatUi.Presentation.CallScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cometchatUi.Presentation.InitialsAvatar


@Composable
fun CallScreen(
    name: String,
    profileImageUrl: String?, // null or empty if not available
    onEndCall: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C)), // Dark background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "calling ...",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar with fallback initials
            if (!profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3F3F3F))
                )
            } else {
                InitialsAvatar(name = name, size = 120.dp)
            }
        }

        IconButton(
            onClick = onEndCall,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .size(64.dp)
                .background(Color.Red, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = "End Call",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CallScreenPreview(){
    CallScreen(
        name = "John Doe",
        profileImageUrl = null,
        onEndCall = {}
    )
}