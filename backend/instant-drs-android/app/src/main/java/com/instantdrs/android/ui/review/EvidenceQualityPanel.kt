package com.instantdrs.android.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.instantdrs.android.model.EvidenceQualityResponse

@Composable
fun EvidenceQualityPanel(quality: EvidenceQualityResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Evidence Quality",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Readiness: ${quality.readiness}")
            
            if (quality.reasonCodes.isNotEmpty()) {
                Text(text = "Reasons:")
                quality.reasonCodes.forEach { code ->
                    val explanation = getHumanReadableReason(code)
                    if (explanation.isNotEmpty()) {
                        Text(
                            text = "• $code: $explanation",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    } else {
                        Text(
                            text = "• $code",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tracking: ${quality.tracking.validPoints} / ${quality.tracking.totalPoints} points")
            Text(text = "Trajectory: ${quality.trajectory.trajectorySegmentCount} segments")
            Text(text = "Bounce Candidates: ${quality.bounce.candidateCount}")
            Text(text = "Impact Candidates: ${quality.impact.candidateCount}")
            
            quality.overallEvidenceQuality?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Overall Quality Score: ${String.format("%.2f", it)}",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
