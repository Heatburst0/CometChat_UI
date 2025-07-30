package com.example.cometchatUi.Presentation.ChatScreen

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.cometchatUi.MessageAction

@Composable
fun MessageActionsPopup(
    onActionSelected: (MessageAction) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss
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

        actions.forEach { action ->
            DropdownMenuItem(
                text = { Text(action.label) },
                onClick = {
                    onActionSelected(action)
                    onDismiss()
                },
                leadingIcon = {
                    Icon(action.icon, contentDescription = action.label)
                }
            )
        }
    }
}
