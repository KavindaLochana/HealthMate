package com.example.healthmate.feature.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthmate.R
import com.example.healthmate.core.ui.components.ActivityRings
import com.example.healthmate.core.ui.components.RingSpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// -----------------------------------------------------------------------------
// UI-only model
// -----------------------------------------------------------------------------

private data class DashboardMetric(
    val title: String,
    val value: Int,
    val target: Int,
    val unit: String,
    val icon: ImageVector,
    val color: Color
) {
    val fraction: Float
        get() = if (target > 0) {
            (value.toFloat() / target).coerceIn(0f, 1f)
        } else {
            0f
        }

    val percentage: Int
        get() = if (target > 0) {
            (value.toFloat() / target * 100).toInt()
        } else {
            0
        }
}

// -----------------------------------------------------------------------------
// Dashboard screen
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    onAddRecordClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val metrics = remember {
        listOf(
            DashboardMetric(
                title = "Steps",
                value = 8240,
                target = 10000,
                unit = "steps",
                icon = Icons.Default.DirectionsWalk,
                color = Color(0xFF4CAF50)
            ),
            DashboardMetric(
                title = "Calories",
                value = 1650,
                target = 2200,
                unit = "kcal",
                icon = Icons.Default.LocalFireDepartment,
                color = Color(0xFFFF7043)
            ),
            DashboardMetric(
                title = "Water",
                value = 1500,
                target = 2000,
                unit = "ml",
                icon = Icons.Default.WaterDrop,
                color = Color(0xFF42A5F5)
            )
        )
    }

    DashboardContent(
        metrics = metrics,
        onAddRecordClick = onAddRecordClick,
        modifier = modifier
    )
}

// -----------------------------------------------------------------------------
// Dashboard content
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DashboardContent(
    metrics: List<DashboardMetric>,
    onAddRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()
            )
        )
    }

    val ringDescription = metrics.joinToString(
        separator = ", "
    ) {
        "${it.title}: ${it.percentage}%"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 100.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Header
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = today,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Activity rings
            ActivityRings(
                rings = metrics.map {
                    RingSpec(
                        fraction = it.fraction,
                        color = it.color
                    )
                },
                description = ringDescription,
                modifier = Modifier
                    .widthIn(max = 240.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Daily progress heading
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Daily progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Your activity for today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Metric cards
            metrics.forEach { metric ->
                MetricProgressCard(
                    metric = metric,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Add record button
        ExtendedFloatingActionButton(
            onClick = onAddRecordClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            },
            text = {
                Text(stringResource(R.string.editor_title_new))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// Metric progress card
// -----------------------------------------------------------------------------

@Composable
private fun MetricProgressCard(
    metric: DashboardMetric,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Metric title and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = metric.icon,
                        contentDescription = null,
                        tint = metric.color
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = metric.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${metric.percentage}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = metric.color
                )
            }

            // Current value and target
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "%,d".format(metric.value),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "/ %,d ${metric.unit}".format(metric.target),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            // Progress bar
            androidx.compose.material3.LinearProgressIndicator(
                progress = { metric.fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = metric.color,
                trackColor = metric.color.copy(alpha = 0.15f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Preview
// -----------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}
