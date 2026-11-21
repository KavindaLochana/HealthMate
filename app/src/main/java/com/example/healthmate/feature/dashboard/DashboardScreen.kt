package com.example.healthmate.feature.dashboard

import android.os.Build
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmate.R
import com.example.healthmate.core.ui.accentColor
import com.example.healthmate.core.ui.components.ActivityRings
import com.example.healthmate.core.ui.components.MetricCard
import com.example.healthmate.core.ui.components.RingSpec
import com.example.healthmate.core.util.toFullDateString
import com.example.healthmate.domain.model.DashboardSummary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onResume() }

    DashboardContent(
        state = state,
        onAddRecordClick = onAddRecordClick,
        modifier = modifier,
    )
}

/** Stateless dashboard: header, animated activity rings and one block per metric. */
@Composable
fun DashboardContent(
    state: DashboardUiState,
    onAddRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().statusBarsPadding()) {
        val summary = state.summary
        if (summary == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            DashboardBody(summary)
        }

        ExtendedFloatingActionButton(
            onClick = onAddRecordClick,
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.editor_title_new)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        )
    }
}

@Composable
private fun DashboardBody(summary: DashboardSummary) {
    val metrics = summary.metrics
    val ringDescription = stringResource(
        R.string.dashboard_rings_description,
        metrics[0].percent,
        metrics[1].percent,
        metrics[2].percent,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = summary.date.toFullDateString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ActivityRings(
            rings = metrics.map { RingSpec(fraction = it.fraction, color = it.metric.accentColor) },
            description = ringDescription,
            modifier = Modifier
                .widthIn(max = 240.dp)
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )

        metrics.forEach { progress ->
            MetricCard(progress = progress, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}
