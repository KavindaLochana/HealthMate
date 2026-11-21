package com.example.healthmate.data.local.db

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

abstract class HealthRecordDao {

    @Query("SELECT * FROM health_records ORDER BY date DESC, id DESC")
    abstract fun observeAll(): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE date = :date ORDER BY id DESC")
    abstract fun observeByDate(date: String): Flow<List<HealthRecordEntity>>

    @Query("SELECT DISTINCT date FROM health_records WHERE date BETWEEN :start AND :end")
    abstract fun observeDatesBetween(start: String, end: String): Flow<List<String>>

    @Query(
        """
        SELECT COALESCE(SUM(steps), 0) AS steps,
               COALESCE(SUM(calories), 0) AS calories,
               COALESCE(SUM(water), 0) AS water
        FROM health_records
        WHERE date = :date
        """,
    )
    abstract fun observeTotals(date: String): Flow<DailyTotalsTuple>

    @Query("SELECT * FROM health_records WHERE id = :id")
    abstract suspend fun getById(id: Long): HealthRecordEntity?

    @Insert
    abstract suspend fun insert(entity: HealthRecordEntity): Long

    @Insert
    abstract suspend fun insertAll(entities: List<HealthRecordEntity>)

    @Update
    abstract suspend fun update(entity: HealthRecordEntity)

    @Query("DELETE FROM health_records WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("DELETE FROM health_records")
    abstract suspend fun deleteAll()

    /** Delete + insert run in one transaction so a failure never leaves the table half-empty. */
    @Transaction
    open suspend fun replaceAll(entities: List<HealthRecordEntity>) {
        deleteAll()
        insertAll(entities)
    }

}