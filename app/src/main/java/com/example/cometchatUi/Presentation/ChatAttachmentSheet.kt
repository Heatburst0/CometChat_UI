package com.example.cometchatUi.Presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cometchatUi.Model.ChatActionOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAttachmentSheet(
    options: List<ChatActionOption>,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.Black,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { option.onClick() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.label,
                        tint = Color(0xFF9B51E0), // Purple
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = option.label,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatAttachmentPreview(){
    ChatAttachmentSheet(
        options = listOf(
            ChatActionOption(
                icon = Icons.Default.Mic,
                label = "sample",
                onClick = {}
            ),
            ChatActionOption(
                icon = Icons.Default.Mic,
                label = "sample",
                onClick = {}
            ),
            ChatActionOption(
                icon = Icons.Default.Mic,
                label = "sample",
                onClick = {}
            )
        ),
    ) { }
}
