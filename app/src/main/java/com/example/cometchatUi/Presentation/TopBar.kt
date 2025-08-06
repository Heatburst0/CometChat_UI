package com.example.cometchatUi.Presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cometchatUi.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onCreateClick: () -> Unit,
    selectedTab: String = "chats"
) {
    var expanded by remember { mutableStateOf(false) }
    val customColors = LocalCustomColors.current
    Column {
        TopAppBar(
            title = {
                when(selectedTab){
                    "chats" ->{
                        Text("Chats", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                    }
                    "calls" ->{
                        Text("Calls", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                    }
                    "contacts" ->{
                        Text("Users", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                    }
                    "groups" ->{
                        Text("Groups", style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                    }
                }
            },
            actions = {
                when(selectedTab){
                    "groups" -> {
                        IconButton(onClick = {
                            onCreateClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = "Create Group",
                                modifier = Modifier.size(28.dp),
                                tint = Color(0xFF9C27B0)
                            )
                        }
                    }
                    else ->{
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(35.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(240.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Create conversation") },
                                onClick = {
                                    onCreateClick()
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Add, contentDescription= null)
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Clinton Wilkinson") },
                                onClick = {
                                    // Handle name
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("V5.0.2", color = Color.Gray) },
                                onClick = { expanded = false },
                                enabled = false
                            )
                        }
                    }
                }

            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = customColors.background
            )
        )

        HorizontalDivider()
    }
}

