package com.example.cometchatUi

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cometchatUi.ChatRepository.sendCallLogMessage
import com.example.cometchatUi.Presentation.CallScreen.CallScreen
import com.example.cometchatUi.ui.theme.CometChat_UITheme

class CallActivity : ComponentActivity() {

    private var ringtone : Ringtone?=null
    private lateinit var currentUserId : String
    private lateinit var receiverId : String
    private lateinit var contactname : String
    private lateinit var chatId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactname= (if(intent.hasExtra("contactName")) intent.getStringExtra("contactName") else "").toString()
        currentUserId = (if(intent.hasExtra("currentUserId")) intent.getStringExtra("currentUserId") else "").toString()
        receiverId= (if(intent.hasExtra("receiverId")) intent.getStringExtra("receiverId") else "").toString()
        chatId= (if(intent.hasExtra("chatId")) intent.getStringExtra("chatId") else "").toString()

        playSystemRingtone()
        sendCallLogMessage(
            chatId = chatId,
            senderId = currentUserId,
            receiverId = receiverId,
            senderName = "You",
            receiverName = contactname,
            callType = "Outgoing Call"
        )
        setContent {
            CometChat_UITheme {
                CallScreen(
                    name = contactname,
                    profileImageUrl = null,
                    onEndCall = {
                        sendCancelledCallLog()
                        finish()
                    }
                )
            }
        }
    }

    private fun playSystemRingtone() {
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, uri)
        ringtone?.play()
    }

    private fun stopSystemRingtone() {
        ringtone?.stop()
        ringtone = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSystemRingtone()
    }

    private fun sendCancelledCallLog(){
        sendCallLogMessage(
            chatId = chatId,
            senderId = currentUserId,
            receiverId = receiverId,
            senderName = "You",
            receiverName = contactname,
            callType = "Call Cancelled"
        )
    }
}