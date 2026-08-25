package com.example.instantdrs_android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.instantdrs_android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.instantdrs_android.ui.components.InstantDRSButton
import com.example.instantdrs_android.ui.components.InstantDRSCard
import com.example.instantdrs_android.ui.components.InstantDRSScreenContainer
import com.example.instantdrs_android.ui.components.InstantDRSStatusBadge
import com.example.instantdrs_android.ui.theme.InstantDRSAndroidTheme
import com.example.instantdrs_android.ui.theme.LocalSpacing

data class MockSport(val name: String, val status: String, val rules: String, val imageRes: Int)

val mockSports = listOf(
    MockSport("Volleyball", "DRS Available", "Ball In/Out • Net Touch", R.mipmap.volleyball),
    MockSport("Tennis", "DRS Available", "Line/Out Review", R.mipmap.tennis),
    MockSport("Cricket", "DRS Available", "Decision Review", R.mipmap.cricket)
)

@Composable
fun HomeScreen(
    onGameClick: (String) -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    InstantDRSScreenContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HOME",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = LocalSpacing.current.large)
            )

            Text(
                text = "GAMES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = LocalSpacing.current.medium)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.medium)
            ) {
                items(mockSports) { sport ->
                    InstantDRSCard(
                        modifier = Modifier.clickable { onGameClick(sport.name) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(sport.imageRes),
                                contentDescription = sport.name,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = sport.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                                InstantDRSStatusBadge(
                                    text = sport.status,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(LocalSpacing.current.medium))
                                Text(
                                    text = sport.rules,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(LocalSpacing.current.large))

            InstantDRSButton(
                text = "HISTORY",
                onClick = onHistoryClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    InstantDRSAndroidTheme {
        HomeScreen()
    }
}
