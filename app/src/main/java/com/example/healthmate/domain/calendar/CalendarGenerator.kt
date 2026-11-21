package com.example.healthmate.domain.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


// calendar generator for history screen
object CalendarGenerator {

    fun monthGrid(
        month: YearMonth,
        firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    ): List<List<LocalDate?>> {
        val leadingBlanks = (month.atDay(1).dayOfWeek.value - firstDayOfWeek.value + 7) % 7
        val cells = buildList<LocalDate?> {
            repeat(leadingBlanks) { add(null) }
            for (day in 1..month.lengthOfMonth()) add(month.atDay(day))
            while (size % DAYS_IN_WEEK != 0) add(null)
        }
        return cells.chunked(DAYS_IN_WEEK)
    }

    // header row
    fun weekdays(firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY): List<DayOfWeek> =
        List(DAYS_IN_WEEK) { firstDayOfWeek.plus(it.toLong()) }

    private const val DAYS_IN_WEEK = 7
}