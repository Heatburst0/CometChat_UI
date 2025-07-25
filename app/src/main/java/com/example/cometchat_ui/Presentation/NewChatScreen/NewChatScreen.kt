package com.example.cometchat_ui.Presentation.NewChatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cometchat_ui.ui.theme.LocalCustomColors

@Composable
fun NewChatScreen(
    onBackClick: () -> Unit
) {

    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize()
            .background(customColors.background)) {
            NewChatTopBar(onBackClick =  onBackClick )
            RoundedTabRow(
                selectedTabIndex = selectedTab,
                titles = listOf("Users", "Groups"),
                onTabSelected = { selectedTab = it }
            )
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

            LazyColumn {

            }
        }


}

@Preview(showBackground = true)
@Composable
fun NewChatScreenPreview(){
    NewChatScreen(
        onBackClick = {}
    )
}