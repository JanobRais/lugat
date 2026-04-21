package com.lugat.app.repository

import android.content.Context
import com.lugat.app.data.dao.LugatDao
import com.lugat.app.data.entity.Mistake
import com.lugat.app.data.entity.Progress
import com.lugat.app.data.entity.Word
import com.lugat.app.model.LanguageDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class LugatRepository(
    private val dao: LugatDao,
    private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("lugat_prefs", Context.MODE_PRIVATE)

    var dailyWordLimit: Int
        get() = sharedPreferences.getInt("daily_word_limit", 15)
        set(value) = sharedPreferences.edit().putInt("daily_word_limit", value).apply()

    var languageDirection: LanguageDirection
        get() {
            val name = sharedPreferences.getString("language_direction", LanguageDirection.RU_UZ.name)
            return try {
                LanguageDirection.valueOf(name!!)
            } catch (e: Exception) {
                LanguageDirection.RU_UZ
            }
        }
        set(value) = sharedPreferences.edit().putString("language_direction", value.name).apply()

    suspend fun checkAndPopulateDatabase() = withContext(Dispatchers.IO) {
        if (dao.getWordCount() == 0) {
            val words = mutableListOf<Word>()
            try {
                val inputStream = context.assets.open("russian_uzbek_english_dictionary.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))
                // Skip header: id,ru,uz,en
                reader.readLine()
                
                var idCounter = 1
                var line: String? = reader.readLine()
                while (line != null) {
                    val parts = line.split(",")
                    if (parts.size >= 3) {
                        try {
                            val ru = parts[0].trim()
                            val uz = parts[1].trim()
                            val en = parts[2].trim()
                            words.add(Word(idCounter, ru, uz, en))
                            idCounter++
                        } catch (e: Exception) {
                            // ignore malformed line
                        }
                    }
                    line = reader.readLine()
                }
                reader.close()
                if (words.isNotEmpty()) {
                    dao.insertWords(words)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getDailyWords(): List<Word> = withContext(Dispatchers.IO) {
        dao.getNewWords(dailyWordLimit)
    }

    suspend fun markWordsAsLearned(words: List<Word>) = withContext(Dispatchers.IO) {
        val time = System.currentTimeMillis()
        val progresses = words.map { Progress(it.id, true, time) }
        dao.insertProgress(progresses)
    }

    suspend fun reportMistake(wordId: Int) = withContext(Dispatchers.IO) {
        val existing = dao.getMistake(wordId)
        if (existing != null) {
            dao.incrementMistake(wordId)
        } else {
            dao.insertMistake(Mistake(wordId, 1))
        }
    }

    suspend fun getMistakeWords(limit: Int): List<Word> = withContext(Dispatchers.IO) {
        dao.getMistakeWords(limit)
    }

    suspend fun getOldLearnedWords(limit: Int): List<Word> = withContext(Dispatchers.IO) {
        dao.getOldLearnedWords(limit)
    }

    suspend fun getRandomOptions(excludeId: Int, limit: Int): List<Word> = withContext(Dispatchers.IO) {
        dao.getRandomOptions(excludeId, limit)
    }
}
