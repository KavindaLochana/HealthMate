package com.example.healthmate.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.ui.accentColor
import com.example.healthmate.core.ui.icon
import com.example.healthmate.core.ui.labelRes
import com.example.healthmate.core.ui.valueFormatRes
import com.example.healthmate.core.util.formatNumber
import com.example.healthmate.domain.model.MetricProgress
import com.example.healthmate.R

// one metric ui

@Composable
fun MetricCard(
    progress: MetricProgress,
    modifier: Modifier = Modifier,
) {
    val accent = progress.metric.accentColor
    Card(
        modifier = modifier.semantics(mergeDescendants = true) {},
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = progress.metric.icon,
                    contentDescription = null,
                    tint = accent,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(progress.metric.labelRes),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(
                        R.string.dashboard_percent_of_target,
                        progress.percent,
                        formatNumber(progress.target),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(progress.metric.valueFormatRes, formatNumber(progress.current)),
                style = MaterialTheme.typography.titleLarge,
                color = accent,
            )
        }
    }
}
