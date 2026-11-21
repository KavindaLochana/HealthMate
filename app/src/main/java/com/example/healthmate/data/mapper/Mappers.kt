package com.example.healthmate.data.mapper

import com.example.healthmate.data.local.db.DailyTotalsTuple
import com.example.healthmate.data.local.db.HealthRecordEntity
import com.example.healthmate.data.local.db.UserEntity
import com.example.healthmate.domain.model.DailyTotals
import com.example.healthmate.domain.model.HealthRecord
import com.example.healthmate.domain.model.User
import java.time.LocalDate

fun HealthRecordEntity.toDomain(): HealthRecord = HealthRecord(
    id = id,
    date = LocalDate.parse(date),
    steps = steps,
    calories = calories,
    water = water,
)

fun HealthRecord.toEntity(): HealthRecordEntity = HealthRecordEntity(
    id = id,
    date = date.toString(),
    steps = steps,
    calories = calories,
    water = water,
)

fun DailyTotalsTuple.toDomain(date: LocalDate): DailyTotals = DailyTotals(
    date = date,
    steps = steps,
    calories = calories,
    water = water,
)

fun UserEntity.toDomain(): User = User(id = id, name = name, email = email)
