package com.lugat.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "essential_progress")
data class EssentialProgress(
    @PrimaryKey
    val wordId: Int,
    val isLearned: Boolean,
    val timestamp: Long,
    val nextReviewDate: Long = 0L,  // For Spaced Repetition (0 means not scheduled yet)
    val intervalDays: Int = 0       // Current SRS interval
)
