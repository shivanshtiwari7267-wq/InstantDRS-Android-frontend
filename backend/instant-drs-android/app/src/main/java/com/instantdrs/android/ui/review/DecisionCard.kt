package com.instantdrs.android.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.instantdrs.android.model.DrsReviewResponse

@Composable
fun DecisionCard(review: DrsReviewResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "DRS Decision",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val decisionText = review.decision ?: "UNKNOWN"
            Text(
                text = decisionText,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Black
            )
            
            review.reasonCode?.let { reasonCode ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reason: $reasonCode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                val explanation = getHumanReadableReason(reasonCode)
                if (explanation.isNotEmpty()) {
                    Text(
                        text = "→ $explanation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

fun getHumanReadableReason(reasonCode: String): String {
    return when (reasonCode) {
        "BOUNCE_MISSING" -> "A reliable bounce event could not be established from the available evidence."
        "IMPACT_EVIDENCE_MISSING" -> "The exact point of impact with the batter could not be clearly identified."
        "TRACKING_INSUFFICIENT" -> "Not enough valid tracking points were found to form a reliable trajectory."
        "TRAJECTORY_INSUFFICIENT" -> "The ball trajectory could not be reliably calculated."
        "PROJECTION_INSUFFICIENT" -> "The projected path of the ball could not be reliably determined."
        "WICKET_INTERSECTION_UNAVAILABLE" -> "Could not determine if the projected path intersects the wickets."
        "LOW_EVIDENCE_CONFIDENCE" -> "The confidence level of the gathered evidence is too low for a conclusive decision."
        "MULTIPLE_CONFLICTING_CANDIDATES" -> "Multiple conflicting events were detected, preventing a clear conclusion."
        "EVIDENCE_COMPLETE" -> "All required evidence was successfully gathered."
        else -> ""
    }
}
