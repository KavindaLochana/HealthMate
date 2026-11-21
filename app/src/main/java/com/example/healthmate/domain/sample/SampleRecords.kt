package com.example.healthmate.domain.sample

import com.example.healthmate.domain.model.HealthRecord
import java.time.LocalDate


// dummy data
object SampleRecords {

    private data class Entry(val steps: Int, val calories: Int, val water: Int)

    private val previousDays: List<List<Entry>> = listOf(
        listOf(Entry(9_210, 510, 2_400)),
        listOf(Entry(6_480, 340, 1_800)),
        listOf(Entry(4_200, 230, 1_200), Entry(3_900, 200, 1_000)),
        listOf(Entry(11_250, 610, 2_700)),
        listOf(Entry(5_320, 280, 1_500)),
        listOf(Entry(8_040, 430, 2_100)),
        listOf(Entry(10_020, 520, 2_500)),
        listOf(Entry(7_410, 390, 1_900)),
        listOf(Entry(3_890, 210, 1_300)),
        listOf(Entry(9_680, 490, 2_300)),
        listOf(Entry(8_750, 450, 2_200)),
        listOf(Entry(6_120, 320, 1_700)),
        listOf(Entry(10_540, 560, 2_600)),
    )

    val dayCount: Int get() = previousDays.size + 1

    fun generate(today: LocalDate): List<HealthRecord> {
        val todayRecord = HealthRecord(date = today, steps = 7_850, calories = 420, water = 2_000)
        val history = previousDays.flatMapIndexed { index, entries ->
            val date = today.minusDays(index + 1L)
            entries.map {
                HealthRecord(
                    date = date,
                    steps = it.steps,
                    calories = it.calories,
                    water = it.water
                )
            }
        }
        return listOf(todayRecord) + history
    }
}
