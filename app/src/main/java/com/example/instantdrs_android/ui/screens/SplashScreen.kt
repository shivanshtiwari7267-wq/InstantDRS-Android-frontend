package com.example.instantdrs_android.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    val alphaAnimation = remember {
        Animatable(0f)
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    LaunchedEffect(Unit) {
        alphaAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000
            )
        )

        delay(2000L)

        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.alpha(alphaAnimation.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            /*
             * InstantDRS Universal Sports Logo
             *
             * Concept:
             * - Circular motion = video replay/review
             * - Center point = decision point
             * - Arrow = instant review/action
             */
            Canvas(
                modifier = Modifier.size(150.dp)
            ) {

                val center = Offset(
                    x = size.width / 2f,
                    y = size.height / 2f
                )

                val outerRadius = size.minDimension * 0.36f
                val innerRadius = size.minDimension * 0.16f

                // Main circular replay/review ring
                drawArc(
                    color = primaryColor,
                    startAngle = -55f,
                    sweepAngle = 285f,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - outerRadius,
                        center.y - outerRadius
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        outerRadius * 2,
                        outerRadius * 2
                    ),
                    style = Stroke(
                        width = 9.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Second shorter motion arc
                drawArc(
                    color = primaryColor.copy(alpha = 0.45f),
                    startAngle = 125f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - outerRadius * 0.82f,
                        center.y - outerRadius * 0.82f
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        outerRadius * 1.64f,
                        outerRadius * 1.64f
                    ),
                    style = Stroke(
                        width = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Central decision circle
                drawCircle(
                    color = primaryColor,
                    radius = innerRadius,
                    center = center
                )

                // Central white highlight
                drawCircle(
                    color = Color.White,
                    radius = innerRadius * 0.38f,
                    center = center
                )

                // Direction arrow at the top-right
                val arrowPoint = Offset(
                    x = center.x + outerRadius * 0.72f,
                    y = center.y - outerRadius * 0.58f
                )

                val arrowLength = 22.dp.toPx()

                drawLine(
                    color = primaryColor,
                    start = Offset(
                        arrowPoint.x - arrowLength,
                        arrowPoint.y
                    ),
                    end = arrowPoint,
                    strokeWidth = 7.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = primaryColor,
                    start = arrowPoint,
                    end = Offset(
                        arrowPoint.x - 9.dp.toPx(),
                        arrowPoint.y - 9.dp.toPx()
                    ),
                    strokeWidth = 7.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawLine(
                    color = primaryColor,
                    start = arrowPoint,
                    end = Offset(
                        arrowPoint.x - 9.dp.toPx(),
                        arrowPoint.y + 9.dp.toPx()
                    ),
                    strokeWidth = 7.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "InstantDRS",
                color = textColor,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Review. Decide. Instantly.",
                color = secondaryTextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Preview(
    name = "InstantDRS Splash Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SplashScreenPreview() {
    InstantDRSAndroidTheme {
        SplashScreen(
            onTimeout = {}
        )
    }
}