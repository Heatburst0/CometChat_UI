package com.example.cometchat_ui.Presentation.NewChatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cometchat_ui.Model.Contact
import com.example.cometchat_ui.Presentation.ContactItem
import com.example.cometchat_ui.ui.theme.LocalCustomColors

@Composable
fun NewChatScreen(
    contacts: List<Contact>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val customColors = LocalCustomColors.current
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(customColors.background)
    ) {
        NewChatTopBar(onBackClick = onBackClick)
        RoundedTabRow(
            selectedTabIndex = selectedTab,
            titles = listOf("Users", "Groups"),
            onTabSelected = { selectedTab = it }
        )
        SearchBar(query = searchQuery, onQueryChange = onSearchQueryChange)

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF9C27B0))
            }
        } else {
            LazyColumn {
                contacts
                    .groupBy { it.name.first().uppercaseChar() }
                    .toSortedMap()
                    .forEach { (initial, group) ->
                        stickyHeader {
                            Text(
                                text = initial.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(customColors.background)
                                    .padding(8.dp),
                                color = Color(0xFF9C27B0),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(group) { contact ->
                            ContactItem(contact)
                        }
                    }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun NewChatScreenPreview(){
    NewChatScreen(
        listOf<Contact>(
            Contact("Krish", "", true),
            Contact("Krish", "", true),
            Contact("Krish", "", true),
            Contact("Krish", "", true),
            Contact("Krish", "", true),
        ),
        false,
        "",
        onSearchQueryChange = {},
        onBackClick = {}
    )
}