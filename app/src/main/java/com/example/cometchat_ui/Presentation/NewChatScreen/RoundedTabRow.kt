package com.example.cometchat_ui.Presentation.NewChatScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cometchat_ui.ui.theme.LocalCustomColors

@Composable
fun RoundedTabRow(
    selectedTabIndex: Int,
    titles: List<String>,
    onTabSelected: (Int) -> Unit
) {
    val customColors = LocalCustomColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(color = customColors.tabBackground, shape = RoundedCornerShape(30)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        titles.forEachIndexed { index, title ->
            val selected = selectedTabIndex == index

            val backgroundColor = if (selected) customColors.tabSelectedBackground else Color.Transparent
            val contentColor = if (selected) customColors.tabSelectedText else customColors.tabUnselectedText

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(30))
                    .clickable { onTabSelected(index) }
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp
                )
            }
        }
    }
}
