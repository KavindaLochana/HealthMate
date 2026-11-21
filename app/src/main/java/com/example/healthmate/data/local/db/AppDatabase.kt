package com.example.healthmate.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [HealthRecordEntity::class, UserEntity::class],
    version = 1,
    exportSchema = true,
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun userDao(): UserDao

    companion object {

    }
}