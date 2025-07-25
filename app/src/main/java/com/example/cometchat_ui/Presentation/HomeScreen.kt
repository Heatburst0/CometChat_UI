package com.example.cometchat_ui.Presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cometchat_ui.ui.theme.CometChat_UITheme
import com.example.cometchat_ui.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateClick: () -> Unit
){
    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableStateOf("chats") }

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
                // Main content like chat list here
                ChatList(modifier = Modifier.padding(innerPadding))
            }
        }


}

@Composable
fun ChatList(modifier: Modifier) {

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview(){

    HomeScreen(
        onCreateClick = {}
    )
}