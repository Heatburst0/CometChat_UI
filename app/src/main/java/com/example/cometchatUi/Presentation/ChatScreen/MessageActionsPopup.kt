package com.example.cometchatUi.Presentation.ChatScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.cometchatUi.MessageAction

@Composable
fun MessageActionsPopup(
    onActionSelected: (MessageAction) -> Unit,
    onDismiss: () -> Unit
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

    Surface(
        color = Color(0xFF2C2C2C),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            actions.forEach { action ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onActionSelected(action)
                            onDismiss()
                        }
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.label,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = action.label, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun preview(){
    MessageActionsPopup(
        onActionSelected = { /* Handle action selection */ },
        onDismiss = { /* Dismiss the popup */ }
    )
}
