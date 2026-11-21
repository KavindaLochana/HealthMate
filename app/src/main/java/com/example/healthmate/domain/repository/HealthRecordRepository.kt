package com.example.healthmate.domain.repository

import com.example.healthmate.domain.model.DailyTotals
import com.example.healthmate.domain.model.HealthRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

// contract for reading and writing
interface HealthRecordRepository {
    // all records newest date first
    fun observeAll(): Flow<List<HealthRecord>>

    //records for one day
    fun observeByDate(date: LocalDate): Flow<List<HealthRecord>>

    //days fo month records
    fun observeDatesWithRecords(month: YearMonth): Flow<Set<LocalDate>>

    //total records for date
    fun observeTotals(date: LocalDate): Flow<DailyTotals>

    suspend fun getById(id: Long): HealthRecord?
    suspend fun insert(record: HealthRecord): Long
    suspend fun update(record: HealthRecord)
    suspend fun delete(id: Long)

    // replace all records - mainly for sample data
    suspend fun replaceAll(records: List<HealthRecord>)
}