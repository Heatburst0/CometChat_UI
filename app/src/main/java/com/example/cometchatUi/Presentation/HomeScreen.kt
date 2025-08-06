package com.example.cometchatUi.Presentation

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.cometchatUi.Presentation.CallScreen.CallLogsScreen
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import com.example.cometchatUi.ChatRepository
import com.example.cometchatUi.Model.Contact
import com.example.cometchatUi.Presentation.NewChatScreen.NewChatScreen
import com.example.cometchatUi.utils.ContactUtils
import java.util.jar.Manifest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentUserId: String,
    onCreateClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableStateOf("chats") }
    val context = LocalContext.current
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var anchorPosition by remember { mutableStateOf<Offset?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showCreateGroupSheet by remember { mutableStateOf(false) }

    // Fetch chat summaries on resume
    val lifecycleOwner = LocalLifecycleOwner.current

    // Store contacts and searchQuery state
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredContacts by remember {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                contacts
            } else {
                contacts.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }
    var permissionGranted by remember { mutableStateOf(false) }

    // Permission launcher

    val contactPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            coroutineScope.launch {
                contacts = ContactUtils.fetchAllContactsFast(context)
            }
        } else {
            Toast.makeText(context, "Contacts permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(selectedTab) {
        if (selectedTab == "contacts" && !permissionGranted) {
            contactPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }
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

    // Modal Bottom Sheet
    if (showCreateGroupSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateGroupSheet = false },
            sheetState = modalSheetState,
            containerColor = Color(0xFF1A1A1A),
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
        ) {
            CreateGroupBottomSheetContent(
                onCreateGroup = { groupType, groupName ->
                    ChatRepository.createGroup(
                        groupName,
                        currentUserId,
                        "You",
                        onSuccess = {
                            Toast.makeText(context, "Group created successfully", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to create group", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                    coroutineScope.launch {
                        modalSheetState.hide()
                        showCreateGroupSheet = false
                    }
                },
                onClose = {
                    coroutineScope.launch {
                        modalSheetState.hide()
                        showCreateGroupSheet = false
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onCreateClick = {
                    if (selectedTab == "groups") {
                        showCreateGroupSheet = true
                        coroutineScope.launch { modalSheetState.show() }
                    } else {
                        onCreateClick()
                    }
                },
                selectedTab = selectedTab
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = customColors.background
    ) { innerPadding ->
        when (selectedTab) {
            "chats" -> {
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
                                            if(summary.isGroup){
                                                Toast.makeText(context, "Group clicked", Toast.LENGTH_SHORT).show()
                                                intent.putExtra("groupId", summary.chatId)
                                                intent.putExtra("isGroup", summary.isGroup)
                                            }
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
                                    modifier = Modifier.wrapContentSize(Alignment.TopEnd)
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

            "calls" -> {
                Box(modifier = Modifier.padding(innerPadding)) {
                    CallLogsScreen(currentUserId = currentUserId) {}
                }
            }
            "contacts" ->{
                Box(modifier = Modifier.padding(innerPadding)){
                    NewChatScreen(
                        contacts = filteredContacts,
                        isLoading = !permissionGranted,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onBackClick = { selectedTab = "chats" },
                        UsersScreen = true
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupBottomSheetContent(
    onCreateGroup: (String, String) -> Unit,
    onClose: () -> Unit
) {
    val groupTypes = listOf("Public", "Private", "Protected")
    var selectedType by remember { mutableStateOf("Public") }
    var groupName by remember { mutableStateOf("") }
    val customColors = LocalCustomColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .imePadding(),
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

        Text(
            "Type",
            fontWeight = FontWeight.SemiBold,
            color = customColors.basicText,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                    Text(text = type, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Name",
            fontWeight = FontWeight.SemiBold,
            color = customColors.basicText,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Button(
            onClick = {
                onCreateGroup(selectedType, groupName)
                onClose()
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