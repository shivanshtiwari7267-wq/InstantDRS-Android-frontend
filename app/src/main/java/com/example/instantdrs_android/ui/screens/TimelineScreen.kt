package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.*
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

data class TimelineEvent(
    val time: String,
    val title: String,
    val description: String,
    val result: String? = null
)

@Composable
fun TimelineScreen(
    sportName: String,
    onReplayEventClick: (TimelineEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    val events = listOf(
        TimelineEvent("00:00", "RECORDING START", "Recording started"),
        TimelineEvent("00:42", "DRS EVENT", "Ball approaching line"),
        TimelineEvent("00:45", "REVIEW MOMENT", "Possible Ball Out"),
        TimelineEvent("00:48", "DECISION", "BALL IN", "BALL IN"),
        TimelineEvent("01:20", "DRS EVENT", "Net Touch Check")
    )

    var selectedEventIndex by remember { mutableStateOf(0) }

    InstantDRSScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "DRS TIMELINE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = sportName.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            // Timeline Card
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.medium)) {
                Text(
                    text = "REVIEW TIMELINE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )

                events.forEachIndexed { index, event ->
                    TimelineItem(
                        event = event,
                        isLast = index == events.size - 1,
                        isSelected = index == selectedEventIndex,
                        onClick = { selectedEventIndex = index }
                    )
                }
            }

            // Selected Event Card
            val selectedEvent = events[selectedEventIndex]
            InstantDRSCard(modifier = Modifier.padding(bottom = spacing.large)) {
                Text(
                    text = "SELECTED EVENT",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
                
                ReviewInfoRow(label = "Time", value = selectedEvent.time)
                ReviewInfoRow(label = "Event", value = selectedEvent.title)
                
                if (selectedEvent.result != null) {
                    ReviewInfoRow(label = "Result", value = selectedEvent.result)
                } else {
                    ReviewInfoRow(label = "Description", value = selectedEvent.description)
                }
            }

            // Actions
            InstantDRSButton(
                text = "REPLAY EVENT",
                onClick = { onReplayEventClick(selectedEvent) },
                modifier = Modifier.padding(bottom = spacing.medium)
            )

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(bottom = spacing.medium)
            ) {
                Text(
                    text = "< Back",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TimelineItem(
    event: TimelineEvent,
    isLast: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val indicatorColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = event.time,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Indicator and Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(Color.Gray.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Event Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isLast) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimelineScreenPreview() {
    InstantDRSAndroidTheme {
        TimelineScreen(
            sportName = "Volleyball",
            onReplayEventClick = {},
            onBackClick = {}
        )
    }
}
