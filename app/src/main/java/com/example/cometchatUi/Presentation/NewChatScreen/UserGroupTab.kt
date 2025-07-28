package com.example.cometchatUi.Presentation.NewChatScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserGroupTab(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Users", "Groups")

    TabRow(
        modifier = Modifier
            .padding(start= 12.dp, end = 12.dp),
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFFF5F5F5),
        contentColor = Color(0xFF6A1B9A), // Example purple color
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = Color(0xFF6A1B9A)
            )
        }
    ) {
        tabs.forEachIndexed { index, text ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
