package com.example.cometchatUi

import android.content.Context
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cometchatUi.Model.Contact
import com.example.cometchatUi.Presentation.ChatScreen.ChatScreen
import com.example.cometchatUi.ui.theme.CometChat_UITheme
import java.io.File
import java.io.IOException

class ChatActivity : ComponentActivity() {
    private lateinit var contact: Contact
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        contact = Contact(
            name = intent.getStringExtra("contactName") ?: "",
            profileUrl = "",
            isOnline = true
        )

        val currentUserId = "user_123" // This would be dynamic when you use Firebase Auth
        val receiverId = contact.name.lowercase().replace(" ", "_") // E.g., "John Doe" → "john_doe"
        val chatId = generateChatId(currentUserId, receiverId)
        setContent {
            CometChat_UITheme {
                ChatScreen(
                    contactName = contact.name,
                    profileUrl = contact.profileUrl,
                    isOnline = true,
                    lastSeen = "2 minutes ago",
                    onBackClick = { finish() },

                    // 👇 Required parameters
                    chatId = chatId,
                    chatRepository = ChatRepository,
                    currentUserId = currentUserId,
                    receiverId = receiverId
                )
            }
        }

    }

    private fun generateChatId(user1: String, user2: String): String {
        // Ensures chatId is the same for both users (lexicographically sorted)
        return listOf(user1, user2).sorted().joinToString("_")
    }

    fun startRecording(context: Context): String {
        outputFile = "${context.externalCacheDir?.absolutePath}/voice_${System.currentTimeMillis()}.mp3"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return outputFile
    }


    fun stopRecording(): File? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null
        return if (outputFile.isNotEmpty()) File(outputFile) else null
    }


}