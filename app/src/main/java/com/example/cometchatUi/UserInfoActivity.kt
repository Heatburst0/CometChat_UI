package com.example.cometchatUi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cometchatUi.Presentation.UserInfoScreen.UserInfoScreen
import com.example.cometchatUi.ui.theme.CometChat_UITheme

class UserInfoActivity : ComponentActivity() {
    private lateinit var currentUserId : String
    private lateinit var receiverId : String
    private lateinit var contactname : String
    private lateinit var chatId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactname= (if(intent.hasExtra("contactName")) intent.getStringExtra("contactName") else "").toString()
        receiverId= (if(intent.hasExtra("receiverId")) intent.getStringExtra("receiverId") else "").toString()
        receiverId= (if(intent.hasExtra("receiverId")) intent.getStringExtra("receiverId") else "").toString()
        chatId= (if(intent.hasExtra("chatId")) intent.getStringExtra("chatId") else "").toString()

        setContent{
            CometChat_UITheme {
                UserInfoScreen(

                    userName = contactname,
                    profileUrl = null,
                    isOnline = true,
                    onVoiceCallClick = {
                        val intent = Intent(this, CallActivity::class.java)
                        intent.putExtra("currentUserId", currentUserId)
                        intent.putExtra("receiverId", receiverId)
                        intent.putExtra("contactName", contactname)
                        intent.putExtra("chatId", chatId)
                        startActivity(intent)
                    },
                    onVideoCallClick = {
                        // Handle video call click
                    },
                    onBlockClick = {
                        // Handle block click
                    },
                    onDeleteChatClick = {
                        ChatRepository.deleteChat(
                            currentUserId = currentUserId,
                            receiverId = receiverId
                        )
                        finish()
                    },
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }
}