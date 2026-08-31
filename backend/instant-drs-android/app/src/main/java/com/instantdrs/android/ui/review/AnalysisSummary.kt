package com.instantdrs.android.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.instantdrs.android.model.VideoAnalysisResultResponse

@Composable
fun AnalysisSummary(result: VideoAnalysisResultResponse) {
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
                text = "Analysis Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Valid Tracking Points: ${result.ballTracking.totalDetectedFrames}")
            Text(text = "Trajectory Points: ${result.trajectory.pointCount}")
            Text(text = "Cricket Events: ${result.cricketEvents.eventCount}")
            Text(text = "LBW Analyses: ${result.lbwAnalysis.analysisCount}")
        }
    }
}
