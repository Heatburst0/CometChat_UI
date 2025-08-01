package com.example.cometchatUi.Model

import androidx.compose.ui.graphics.vector.ImageVector

data class ChatActionOption(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)
