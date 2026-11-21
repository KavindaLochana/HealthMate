package com.example.healthmate.data.repository

import com.example.healthmate.data.local.db.HealthRecordDao
import com.example.healthmate.data.mapper.toDomain
import com.example.healthmate.data.mapper.toEntity
import com.example.healthmate.domain.model.DailyTotals
import com.example.healthmate.domain.model.HealthRecord
import com.example.healthmate.domain.repository.HealthRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth

class HealthRecordRepositoryImpl(
    private val dao: HealthRecordDao,
) : HealthRecordRepository {

    override fun observeAll(): Flow<List<HealthRecord>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByDate(date: LocalDate): Flow<List<HealthRecord>> =
        dao.observeByDate(date.toString()).map { list -> list.map { it.toDomain() } }

    override fun observeDatesWithRecords(month: YearMonth): Flow<Set<LocalDate>> =
        dao.observeDatesBetween(month.atDay(1).toString(), month.atEndOfMonth().toString())
            .map { dates -> dates.map(LocalDate::parse).toSet() }

    override fun observeTotals(date: LocalDate): Flow<DailyTotals> =
        dao.observeTotals(date.toString()).map { it.toDomain(date) }

    override suspend fun getById(id: Long): HealthRecord? = dao.getById(id)?.toDomain()

    override suspend fun insert(record: HealthRecord): Long = dao.insert(record.toEntity())

    override suspend fun update(record: HealthRecord) = dao.update(record.toEntity())

    override suspend fun delete(id: Long) = dao.deleteById(id)

    override suspend fun replaceAll(records: List<HealthRecord>) =
        dao.replaceAll(records.map { it.copy(id = HealthRecord.NEW_ID).toEntity() })
}
