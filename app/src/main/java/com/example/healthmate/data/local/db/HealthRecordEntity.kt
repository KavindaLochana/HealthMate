package com.example.healthmate.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "health_records",
    indices = [Index(value = ["date"])],
)

data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val steps: Int,
    val calories: Int,
    val water: Int,
)