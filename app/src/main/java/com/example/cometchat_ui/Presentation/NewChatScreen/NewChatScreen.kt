package com.example.cometchat_ui.Presentation.NewChatScreen

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cometchat_ui.ChatActivity
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
    val context = LocalContext.current

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
                            ContactItem(contact,
                                onClick = {
                                    val intent = Intent(context, ChatActivity::class.java)
                                    intent.putExtra("contactName", contact.name)
                                    context.startActivity(intent)
                                }
                            )
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