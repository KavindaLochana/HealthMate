package com.example.healthmate.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [HealthRecordEntity::class, UserEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun userDao(): UserDao

    companion object {
        const val NAME = "healthmate.db"

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, NAME).build()
    }
}