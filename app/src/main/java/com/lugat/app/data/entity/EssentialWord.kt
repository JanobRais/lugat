package com.lugat.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "essential_words")
data class EssentialWord(
    @PrimaryKey
    val id: Int,
    val bookName: String,
    val unitName: String,
    val en: String,
    val uz: String
)
