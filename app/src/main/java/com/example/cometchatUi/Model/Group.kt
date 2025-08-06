package com.example.cometchatUi.Model

data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val creatorId: String = "",
    val timestamp: Long = 0L,
    val members: Map<String, String> = emptyMap() // userId → userName
)


