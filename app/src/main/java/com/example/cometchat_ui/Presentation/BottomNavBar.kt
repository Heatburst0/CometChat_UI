package com.example.cometchat_ui.Presentation

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import com.example.cometchat_ui.Model.BottomNavItem

@Composable
fun BottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Chats", Icons.Default.Chat, "chats"),
        BottomNavItem("Calls", Icons.Default.Call, "calls"),
        BottomNavItem("Contacts", Icons.Default.Person, "contacts"),
        BottomNavItem("Groups", Icons.Default.Group, "groups")
    )

    NavigationBar {
        items.forEach { item ->
            val selected = selectedTab == item.route

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) Color(0xFF9C27B0) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) Color(0xFF9C27B0) else Color.Gray,
                        fontSize = 10.sp
                    )
                },
                alwaysShowLabel = false // Important: hides label for unselected items
            )
        }
    }
}
