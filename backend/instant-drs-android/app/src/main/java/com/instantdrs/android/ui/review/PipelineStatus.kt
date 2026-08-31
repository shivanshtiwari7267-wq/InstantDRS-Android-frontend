package com.instantdrs.android.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.instantdrs.android.model.VideoPipelineStatusResponse

@Composable
fun PipelineStatus(status: VideoPipelineStatusResponse) {
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
                text = "Pipeline Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Processing: ${status.processingStatus}")
            Text(text = "Analysis: ${status.analysisStatus}")
            
            val total = status.totalFrames
            val processed = status.processedFrames
            if (total != null && processed != null) {
                Text(text = "Frames: $processed / $total")
            }
            
            status.progressPercent?.let {
                val progress = it / 100.0
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${String.format("%.1f", it)}%",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
