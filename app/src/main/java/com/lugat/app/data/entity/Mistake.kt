package com.lugat.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mistakes")
data class Mistake(
    @PrimaryKey
    val wordId: Int,
    val mistakeCount: Int
)
