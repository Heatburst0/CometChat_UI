package com.example.cometchatUi.Presentation.ChatScreen

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.cometchatUi.Model.ChatMessage
import com.example.cometchatUi.Model.RepliedMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.cometchatUi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Files.size
import kotlin.math.absoluteValue

@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    isSender: Boolean,
    message: String,
    timestamp: Long,
    status: String,
    reactions: Map<String, List<String>>,
    onReact: (emoji: String) -> Unit,
    onLongPress: () -> Unit,
    chatMessage: ChatMessage
) {
    val showEmojiBar = remember { mutableStateOf(false) }
    val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    val isDeleted = chatMessage.deleted
    val context = LocalContext.current
    var showFullImage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { showEmojiBar.value = false },
                onLongClick = {
                    if (!isDeleted) onLongPress()
                }
            ),
        horizontalAlignment = if (isSender) Alignment.End else Alignment.Start
    ) {

        if (chatMessage.edited) {
            Text(
                text = "(edited)",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        if (!isDeleted && chatMessage.replyTo != null) {
            Column(
                modifier = Modifier
                    .widthIn(min = 60.dp)
                    .background(
                        if (isSender) Color(0xFF9575CD) else Color(0xFF555555),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = chatMessage.replyTo.senderName,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = chatMessage.replyTo.message,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        if (showEmojiBar.value) {
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .background(Color(0xFF2E2E2E), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("👍", "❤️", "😂", "😍", "😢", "😡").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable {
                                onReact(emoji)
                                showEmojiBar.value = false
                            }
                    )
                }
            }
        }

        Box {
            Column(
                modifier = Modifier
                    .background(
                        color = if (isSender) Color(0xFF7C4DFF) else Color(0xFF444444),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                when (chatMessage.messageType) {

                    // 🎤 AUDIO MESSAGE BUBBLE
                    "audio" -> {
                        AudioMessageBubble(
                            isSender = isSender,
                            audioUrl = chatMessage.mediaUrl.orEmpty(),
                            timestamp = chatMessage.timestamp,
                            status = chatMessage.status // fallback dummy waveform
                        )
                    }
                    "image" -> {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 200.dp) // limit width
                                .aspectRatio(0.7f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showFullImage = true }
                        ) {
                            AsyncImage(
                                model = chatMessage.mediaUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        if (showFullImage) {
                            Dialog(onDismissRequest = { showFullImage = false }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black)
                                        .clickable { showFullImage = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = chatMessage.mediaUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = message,
                            color = if (isDeleted) Color.LightGray else Color.White,
                            fontStyle = if (isDeleted) FontStyle.Italic else FontStyle.Normal
                        )
                    }
                }

                // Timestamp + Ticks
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = formattedTime, fontSize = 10.sp, color = Color.LightGray)
                    if (!isDeleted && isSender) {
                        val icon = when (status) {
                            "sent" -> Icons.Default.Check
                            "delivered", "seen" -> Icons.Default.DoneAll
                            "pending" -> Icons.Outlined.AccessTime
                            else -> null
                        }
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "Status",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 4.dp),
                                tint = if (status == "seen") Color.Cyan else Color.Gray
                            )
                        }
                    }
                }
            }

            // 🎉 Reactions View
            if (!isDeleted && reactions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = 20.dp)
                        .background(Color(0xFF2C2C2C), RoundedCornerShape(16.dp))
                        .clickable {
                            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    reactions.forEach { (emoji, users) ->
                        if (users.isNotEmpty()) {
                            Text(
                                text = "$emoji ${users.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioMessageBubble(
    isSender: Boolean,
    audioUrl: String,
    timestamp: Long,
    status: String,
) {
    val context = LocalContext.current
    val exoPlayer = remember(audioUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
        }
    }

    val isPlaying = remember { mutableStateOf(false) }
    val waveformSamples = remember { mutableStateListOf<Int>() }

    // Listen to playback end and reset state
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    isPlaying.value = false
                    exoPlayer.seekTo(0)
                    exoPlayer.pause()
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Load waveform samples
    LaunchedEffect(audioUrl) {
        val samples = generateWaveformSamples(context, audioUrl)
        waveformSamples.clear()
        waveformSamples.addAll(samples)
    }

    Column(horizontalAlignment = if (isSender) Alignment.End else Alignment.Start) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSender) Color(0xFF7C4DFF) else Color(0xFF444444))
                .padding(12.dp)
                .clickable {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                        isPlaying.value = false
                    } else {
                        exoPlayer.play()
                        isPlaying.value = true
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(Modifier.width(12.dp))

            WaveformView(samples = waveformSamples)

            Spacer(Modifier.width(12.dp))

            val fileSize by produceState(initialValue = "Loading...", audioUrl) {
                value = formatFileSizeFromUrl(audioUrl)
            }

            Text(
                text = fileSize,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun WaveformView(samples: List<Int>) {
    Canvas(modifier = Modifier
        .height(24.dp)
        .width(100.dp)) {
        val barWidth = size.width / samples.size
        samples.forEachIndexed { index, sample ->
            val barHeight = (sample / 255f) * size.height
            drawRect(
                color = Color.White,
                topLeft = Offset(index * barWidth, (size.height - barHeight) / 2),
                size = Size(barWidth, barHeight)
            )
        }
    }
}






@Preview(showBackground = true)
@Composable
fun MessageBubblePreview(){
    MessageBubble(
        modifier = Modifier,
        isSender = true,
        message = "This message is deleted",
        timestamp = System.currentTimeMillis(),
        status = "pending",
        reactions = mapOf(),
        onReact = {},
        onLongPress = {},
        chatMessage = ChatMessage(
            replyTo = RepliedMessage(
                messageId = "123",
                senderName = "You",
                message = "He"
            ),
            deleted = true,
            messageType = "audio"
        )
    )
}

suspend fun formatFileSizeFromUrl(audioUrl: String): String = withContext(Dispatchers.IO) {
    try {
        val url = URL(audioUrl)
        val connection = url.openConnection()
        connection.connect()
        val sizeInBytes = connection.contentLengthLong

        if (sizeInBytes <= 0) return@withContext "Unknown"

        val kb = sizeInBytes / 1024
        val mb = kb / 1024

        return@withContext when {
            mb > 0 -> String.format("%.1f MB", mb + (kb % 1024) / 1024.0)
            else -> "$kb KB"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext "Unknown"
    }
}

suspend fun generateWaveformSamples(context: Context, audioUrl: String): List<Int> {
    return withContext(Dispatchers.IO) {
        val samples = mutableListOf<Int>()
        try {
            val extractor =     MediaExtractor()
            extractor.setDataSource(audioUrl)

            val formatIndex = (0 until extractor.trackCount).firstOrNull {
                extractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
            } ?: return@withContext emptyList()

            extractor.selectTrack(formatIndex)
            val format = extractor.getTrackFormat(formatIndex)

            val maxSamples = 50  // keep waveform light
            val bufferSize = 2048
            val buffer = ByteArray(bufferSize)

            var totalRead = 0
            while (true) {
                val sampleSize = extractor.readSampleData(ByteBuffer.wrap(buffer), 0)
                if (sampleSize < 0) break
                val amplitude = buffer.take(sampleSize).map { it.toInt().absoluteValue }.average().toInt()
                samples.add(amplitude.coerceIn(0, 255))
                totalRead += sampleSize
                extractor.advance()
                if (samples.size >= maxSamples) break
            }

            samples
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}


