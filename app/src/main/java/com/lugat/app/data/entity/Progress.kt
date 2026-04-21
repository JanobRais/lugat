package com.lugat.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class Progress(
    @PrimaryKey
    val wordId: Int,
    val isLearned: Boolean,
    val lastReviewedTimestamp: Long
)
