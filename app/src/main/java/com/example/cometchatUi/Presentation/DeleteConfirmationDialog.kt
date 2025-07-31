package com.example.cometchatUi.Presentation


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Delete Message", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
        text = { Text("Are you sure you want to delete this message?") },
        properties = DialogProperties(dismissOnClickOutside = true)
    )
}

@Preview(showBackground = true)
@Composable
fun DeleteConfirmationDialogPreview(){
    DeleteConfirmationDialog(onConfirm = { /*TODO*/ }, onDismiss = { /*TODO*/ })
}
