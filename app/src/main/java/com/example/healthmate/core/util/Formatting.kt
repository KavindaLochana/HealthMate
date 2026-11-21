package com.example.healthmate.core.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatNumber(value: Int, locale: Locale = Locale.getDefault()): String =
    NumberFormat.getIntegerInstance(locale).format(value)

fun LocalDate.toFullDateString(locale: Locale = Locale.getDefault()): String =
    format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))

fun LocalDate.toShortDateString(locale: Locale = Locale.getDefault()): String =
    format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))

fun YearMonth.toTitleString(locale: Locale = Locale.getDefault()): String =
    format(DateTimeFormatter.ofPattern("LLLL yyyy", locale))

// convert picker date to local
fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.utcMillisToLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
