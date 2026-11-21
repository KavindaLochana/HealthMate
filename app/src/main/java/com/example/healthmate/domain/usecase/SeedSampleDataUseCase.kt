package com.example.healthmate.domain.usecase

import com.example.healthmate.domain.repository.HealthRecordRepository
import com.example.healthmate.domain.repository.SettingsRepository
import java.time.Clock
import java.time.LocalDate


// add dummy data - first login
//class SeedSampleDataUseCase(
//    private val records: HealthRecordRepository,
//    private val settings: SettingsRepository,
//    private val clock: Clock,
//) {
//    suspend operator fun invoke() {
//        if (settings.isSampleDataSeeded()) return
//        records.replaceAll(SampleRecords.generate(LocalDate.now(clock)))
//        settings.markSampleDataSeeded()
//    }
//}
//
//// reset replace with sample data
//class ResetSampleDataUseCase(
//    private val records: HealthRecordRepository,
//    private val clock: Clock,
//) {
//    suspend operator fun invoke() {
//        records.replaceAll(SampleRecords.generate(LocalDate.now(clock)))
//    }
//}
