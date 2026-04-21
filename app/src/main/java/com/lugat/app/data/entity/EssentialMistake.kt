package com.lugat.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "essential_mistakes")
data class EssentialMistake(
    @PrimaryKey
    val wordId: Int,
    var mistakeCount: Int
)
