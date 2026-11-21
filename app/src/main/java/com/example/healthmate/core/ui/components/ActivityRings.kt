package com.example.healthmate.core.ui.components


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RingSpec(val fraction: Float, val color: Color)

// ring animation
@Composable
fun ActivityRings(
    rings: List<RingSpec>,
    description: String,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 18.dp,
    ringGap: Dp = 6.dp,
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val animatedFractions = rings.map { ring ->
        animateFloatAsState(
            targetValue = if (started) ring.fraction.coerceIn(0f, 1f) else 0f,
            animationSpec = tween(durationMillis = 1_200, easing = FastOutSlowInEasing),
            label = "ringProgress",
        ).value
    }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .semantics { contentDescription = description },
    ) {
        val strokePx = strokeWidth.toPx()
        val gapPx = ringGap.toPx()
        rings.forEachIndexed { index, ring ->
            val inset = strokePx / 2f + index * (strokePx + gapPx)
            val topLeft = Offset(inset, inset)
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)

            drawArc(
                color = ring.color.copy(alpha = 0.18f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx),
            )
            drawArc(
                color = ring.color,
                startAngle = -90f,
                sweepAngle = 360f * animatedFractions[index],
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
        }
    }
}
