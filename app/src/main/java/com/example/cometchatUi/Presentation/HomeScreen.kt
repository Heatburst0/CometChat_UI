package com.example.cometchatUi.Presentation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cometchatUi.ViewModels.HomeViewModel
import com.example.cometchatUi.ui.theme.LocalCustomColors
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cometchatUi.ChatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cometchatUi.Presentation.CallScreen.CallLogsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUserId: String,
    onCreateClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
){
    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableStateOf("chats") }
    val context = LocalContext.current
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var anchorPosition by remember { mutableStateOf<Offset?>(null) }
    var showCreateGroupSheet by remember { mutableStateOf(false) }


    // Fetch chat summaries once
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadChatSummaries(currentUserId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

        Column(modifier = Modifier.fillMaxSize()
            .background(customColors.background)) {
            Scaffold(
                topBar = { TopBar(onCreateClick = {
                    if(selectedTab =="groups"){
                        showCreateGroupSheet = true
                    }else{
                        onCreateClick()
                    }
                },
                    selectedTab = selectedTab) },
                bottomBar = {
                    BottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                },
                containerColor = customColors.background
            ) { innerPadding ->
                when(selectedTab){
                    "chats" ->{
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            itemsIndexed(viewModel.chatSummaries) { index, summary ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = { offset ->
                                                    expandedMenuIndex = index
                                                    anchorPosition = offset
                                                },
                                                onTap = {
                                                    val intent = Intent(context, ChatActivity::class.java)
                                                    intent.putExtra("contactName", summary.userName)
                                                    context.startActivity(intent)
                                                }
                                            )
                                        }
                                ) {
                                    ChatListItem(
                                        summary = summary,
                                        userName = summary.userName,
                                        userImage = ""
                                    )

                                    if (expandedMenuIndex == index) {
                                        DropdownMenu(
                                            expanded = true,
                                            onDismissRequest = { expandedMenuIndex = null },
                                            modifier = Modifier
                                                .wrapContentSize(Alignment.TopEnd)
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = null,
                                                            tint = Color.Red
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Delete Chat", color = Color.Red)
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.deleteChat(currentUserId, summary.userName)
                                                    expandedMenuIndex = null
                                                }
                                            )
                                        }
                                    }
                                    Divider()
                                }

                            }
                        }
                    }
                    "calls" ->{
                        Box(modifier = Modifier.padding(innerPadding)){
                            CallLogsScreen(
                                currentUserId = currentUserId
                            ) {

                            }
                        }
                    }
                }

            }

            if (showCreateGroupSheet) {
                CreateGroupBottomSheet(
                    onDismiss = { showCreateGroupSheet = false },
                    onCreateGroup = { groupType, groupName ->

                        Toast.makeText(context, "Group created: $groupName", Toast.LENGTH_SHORT).show()
                    }
                )
            }


        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupBottomSheet(
    onDismiss: () -> Unit,
    onCreateGroup: (String, String) -> Unit // (groupType, groupName)
) {
    val groupTypes = listOf("Public", "Private", "Protected")
    var selectedType by remember { mutableStateOf("Public") }
    var groupName by remember { mutableStateOf("") }
    val customColors = LocalCustomColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color(0xFF121212),
        modifier = Modifier.imePadding() // moves UI above keyboard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = Color(0xFF9C27B0),
                modifier = Modifier
                    .size(60.dp)
                    .padding(bottom = 12.dp)
            )

            Text(
                "New Group",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = customColors.basicText
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Type Label
            Text(
                "Type",
                fontWeight = FontWeight.SemiBold,
                color = customColors.basicText,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Type Buttons - Rectangular
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                groupTypes.forEach { type ->
                    Button(
                        onClick = { selectedType = type },
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == type) Color(0xFF9C27B0) else Color.Transparent,
                            contentColor = if (selectedType == type) Color.White else Color.Gray
                        ),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
                    ) {
                        Text(text = type)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Name Label
            Text(
                "Name",
                fontWeight = FontWeight.SemiBold,
                color = customColors.basicText,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Name Field
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                placeholder = { Text("Enter the group name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Create Group Button
            Button(
                onClick = {
                    onCreateGroup(selectedType, groupName)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
            ) {
                Text("Create Group", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}