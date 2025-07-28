package com.example.cometchatUi.ui.theme

// ui/theme/CustomColors.kt

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val searchBarBackground: Color,
    val tabBackground: Color,
    val tabSelectedBackground: Color,
    val tabSelectedText: Color,
    val tabUnselectedText: Color,
    val background : Color,
    val basicText : Color
)

val LocalCustomColors = staticCompositionLocalOf {
    lightCustomColors() // fallback
}

fun lightCustomColors() = CustomColors(
    searchBarBackground = Color(0xFFF0F0F0),
    tabBackground = Color(0xFFF0F0F0),
    tabSelectedBackground = Color.White,
    tabSelectedText = Color(0xFF6200EE),
    tabUnselectedText = Color.Black,
    background = Color.White,
    basicText = Color.Black
)

fun darkCustomColors() = CustomColors(
    searchBarBackground = Color(0xFF2C2C2C),
    tabBackground = Color(0xFF2C2C2C),
    tabSelectedBackground = Color.Black,
    tabSelectedText = Color(0xFF6200EE),
    tabUnselectedText = Color.White,
    background = Color.Black,
    basicText = Color.White
)
