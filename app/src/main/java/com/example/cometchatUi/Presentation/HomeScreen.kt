package com.example.cometchatUi.Presentation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.cometchatUi.ViewModels.HomeViewModel
import com.example.cometchatUi.ui.theme.LocalCustomColors
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cometchatUi.ChatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


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
            .background(customColors.background)){
            Scaffold(
                topBar = { TopBar(onCreateClick = onCreateClick) },
                bottomBar = { BottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                ) },
                containerColor = customColors.background
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(viewModel.chatSummaries) { summary ->
                        ChatListItem(
                            summary = summary,
                            userName = summary.userName,
                            userImage = "",

                        ){
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("contactName", summary.userName)
                            context.startActivity(intent)
                        }
                        Divider()
                    }
                }
            }
        }


}

@Composable
fun ChatList(modifier: Modifier) {

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview(){


}