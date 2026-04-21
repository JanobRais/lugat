package com.lugat.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey
    val id: Int,
    val ru: String,
    val uz: String,
    val en: String
)
