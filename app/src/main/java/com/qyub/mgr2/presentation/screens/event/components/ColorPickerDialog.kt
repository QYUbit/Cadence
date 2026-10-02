package com.qyub.mgr2.presentation.screens.event.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

val colorLabels = mapOf(
    Color(0xFFF44336) to "Red",
    Color(0xFFE91E63) to "Pink",
    Color(0xFF9C27B0) to "Purple",
    Color(0xFF673AB7) to "Deep Purple",
    Color(0xFF3F51B5) to "Indigo",
    Color(0xFF2196F3) to "Blue",
    Color(0xFF03A9F4) to "Light Blue",
    Color(0xFF00BCD4) to "Cyan",
    Color(0xFF009688) to "Teal",
    Color(0xFF4CAF50) to "Green",
    Color(0xFF8BC34A) to "Light Green",
    Color(0xFFCDDC39) to "Lime",
    Color(0xFFFFEB3B) to "Yellow",
    Color(0xFFFFC107) to "Amber",
    Color(0xFFFF9800) to "Orange",
    Color(0xFFFF5722) to "Deep Orange",
    Color(0xFF795548) to "Brown",
    Color(0xFF9E9E9E) to "Gray",
    Color(0xFF607D8B) to "Blue Gray",
)

@Composable
fun ColorPickerDialog(
    colors: Map<Color, String> = colorLabels,
    selected: Color?,
    onSelect: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    title: String = "Choose event color"
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .heightIn(max = 420.dp)
                ) {
                    items(colors.toList()) { (color, label) ->
                        ColorPickerRow(
                            color = color,
                            label = label,
                            isSelected = selected == color,
                            onClick = { onSelect(color) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPickerRow(
    color: Color,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick, role = Role.Button)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val ringSize = 28.dp
        Box(
            modifier = Modifier
                .size(ringSize)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 4.dp else 3.dp,
                    color = color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}