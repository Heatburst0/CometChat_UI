package com.example.cometchatUi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MessageAction(val label: String, val icon: ImageVector) {
    object Reply : MessageAction("Reply", Icons.Default.Reply)
    object Share : MessageAction("Share", Icons.Default.Share)
    object Copy : MessageAction("Copy", Icons.Default.ContentCopy)
    object Edit : MessageAction("Edit", Icons.Default.Edit)
    object Info : MessageAction("Info", Icons.Default.Info)
    object Delete : MessageAction("Delete", Icons.Default.Delete)
    object Translate : MessageAction("Translate", Icons.Default.Translate)
}