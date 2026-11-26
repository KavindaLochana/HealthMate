package com.example.healthmate.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.healthmate.domain.model.MetricType
import com.example.healthmate.R
import com.example.healthmate.core.ui.theme.CaloriesRed
import com.example.healthmate.core.ui.theme.CaloriesRedDark
import com.example.healthmate.core.ui.theme.StepsGreen
import com.example.healthmate.core.ui.theme.StepsGreenDark
import com.example.healthmate.core.ui.theme.WaterBlue
import com.example.healthmate.core.ui.theme.WaterBlueDark


@get:StringRes
val MetricType.labelRes: Int
    get() = when (this) {
        MetricType.STEPS -> R.string.metric_steps
        MetricType.CALORIES -> R.string.metric_calories
        MetricType.WATER -> R.string.metric_water
    }

@get:StringRes
val MetricType.fieldNameRes: Int
    get() = when (this) {
        MetricType.STEPS -> R.string.field_steps
        MetricType.CALORIES -> R.string.field_calories
        MetricType.WATER -> R.string.field_water
    }

@get:StringRes
val MetricType.unitRes: Int
    get() = when (this) {
        MetricType.STEPS -> R.string.unit_steps
        MetricType.CALORIES -> R.string.unit_calories
        MetricType.WATER -> R.string.unit_water
    }

@get:StringRes
val MetricType.valueFormatRes: Int
    get() = when (this) {
        MetricType.STEPS -> R.string.metric_steps_value
        MetricType.CALORIES -> R.string.metric_calories_value
        MetricType.WATER -> R.string.metric_water_value
    }

val MetricType.icon: ImageVector
    get() = when (this) {
        MetricType.STEPS -> Icons.Filled.DirectionsWalk
        MetricType.CALORIES -> Icons.Filled.LocalFireDepartment
        MetricType.WATER -> Icons.Filled.WaterDrop
    }

val MetricType.accentColor: Color
    @Composable
    get() {
        val dark = isSystemInDarkTheme()
        return when (this) {
            MetricType.STEPS -> if (dark) StepsGreenDark else StepsGreen
            MetricType.CALORIES -> if (dark) CaloriesRedDark else CaloriesRed
            MetricType.WATER -> if (dark) WaterBlueDark else WaterBlue
        }
    }