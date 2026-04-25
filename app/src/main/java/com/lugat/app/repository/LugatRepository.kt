package com.lugat.app.repository

import android.content.Context
import com.lugat.app.data.dao.LugatDao
import com.lugat.app.data.entity.EssentialMistake
import com.lugat.app.data.entity.EssentialProgress
import com.lugat.app.data.entity.EssentialWord
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

    var essentialDailyLimit: Int
        get() = sharedPreferences.getInt("essential_daily_limit", 20)
        set(value) = sharedPreferences.edit().putInt("essential_daily_limit", value).apply()

    var languageDirection: LanguageDirection
        get() {
            val name = sharedPreferences.getString("language_direction", LanguageDirection.EN_UZ.name)
            return try {
                LanguageDirection.valueOf(name!!)
            } catch (e: Exception) {
                LanguageDirection.EN_UZ
            }
        }
        set(value) = sharedPreferences.edit().putString("language_direction", value.name).apply()

    var activeDictionaryType: String
        get() = sharedPreferences.getString("active_dictionary", "trilingual_2000") ?: "trilingual_2000"
        set(value) = sharedPreferences.edit().putString("active_dictionary", value).apply()

    var lastLoginDate: String
        get() = sharedPreferences.getString("last_login_date", "") ?: ""
        set(value) = sharedPreferences.edit().putString("last_login_date", value).apply()

    var streakCount: Int
        get() = sharedPreferences.getInt("streak_count", 0)
        set(value) = sharedPreferences.edit().putInt("streak_count", value).apply()

    // ── Accuracy tracking ─────────────────────────────────────────────────
    var totalAnswered: Int
        get() = sharedPreferences.getInt("total_answered", 0)
        set(value) = sharedPreferences.edit().putInt("total_answered", value).apply()

    var totalCorrect: Int
        get() = sharedPreferences.getInt("total_correct", 0)
        set(value) = sharedPreferences.edit().putInt("total_correct", value).apply()

    val accuracyPercent: Int
        get() = if (totalAnswered == 0) 0 else (totalCorrect * 100 / totalAnswered)

    fun recordAnswer(correct: Boolean) {
        totalAnswered += 1
        if (correct) totalCorrect += 1
    }

    // ── Today's learned words count ───────────────────────────────────────
    var todayLearnedCount: Int
        get() {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val saved = sharedPreferences.getString("today_learned_date", "") ?: ""
            return if (saved == today) sharedPreferences.getInt("today_learned_count", 0) else 0
        }
        set(value) {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            sharedPreferences.edit()
                .putString("today_learned_date", today)
                .putInt("today_learned_count", value)
                .apply()
        }

    fun incrementTodayLearned(count: Int) {
        todayLearnedCount = todayLearnedCount + count
    }

    // ── Weekly activity (last 7 days) ─────────────────────────────────────
    fun getWeeklyActivity(): List<Boolean> {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val cal = java.util.Calendar.getInstance()
        val activeDates = sharedPreferences.getStringSet("active_dates", emptySet()) ?: emptySet()
        return (6 downTo 0).map { daysAgo ->
            cal.time = java.util.Date()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
            sdf.format(cal.time) in activeDates
        }
    }

    fun markTodayActive() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val dates = (sharedPreferences.getStringSet("active_dates", emptySet()) ?: emptySet()).toMutableSet()
        dates.add(today)
        // Keep only last 30 days
        if (dates.size > 30) {
            val sorted = dates.sorted()
            dates.removeAll(sorted.take(dates.size - 30).toSet())
        }
        sharedPreferences.edit().putStringSet("active_dates", dates).apply()
    }

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

    suspend fun checkAndPopulateEssentialDatabase() = withContext(Dispatchers.IO) {
        if (dao.getEssentialWordCount() == 0) {
            val words = mutableListOf<EssentialWord>()
            try {
                val inputStream = context.assets.open("essential_4000.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))
                
                // Skip header: Essential,Unit,English,Uzbek
                reader.readLine()
                
                var idCounter = 1
                var line: String? = reader.readLine()
                while (line != null) {
                    val parts = line.split(",")
                    if (parts.size >= 4) {
                        try {
                            val book = parts[0].trim()
                            val unit = parts[1].trim()
                            val en = parts[2].trim()
                            val uz = parts[3].trim()
                            words.add(EssentialWord(idCounter, book, unit, en, uz))
                            idCounter++
                        } catch (e: Exception) {
                            // ignore malformed line
                        }
                    }
                    line = reader.readLine()
                }
                reader.close()
                if (words.isNotEmpty()) {
                    dao.insertEssentialWords(words)
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
        incrementTodayLearned(words.size)
    }

    // ── Word of the Day ───────────────────────────────────────────────────
    suspend fun getWordOfDay(): Word? = withContext(Dispatchers.IO) {
        val count = dao.getWordCount()
        if (count == 0) return@withContext null
        val dayIndex = (java.util.Date().time / 86400000L % count).toInt() + 1
        dao.getWordById(dayIndex)
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

    // ESSENTIAL WRAPPERS
    suspend fun getEssentialBooks(): List<String> = withContext(Dispatchers.IO) {
        dao.getEssentialBooks()
    }

    suspend fun getEssentialUnitsForBook(book: String): List<String> = withContext(Dispatchers.IO) {
        dao.getEssentialUnitsForBook(book)
    }

    suspend fun getNewEssentialWords(limit: Int): List<EssentialWord> = withContext(Dispatchers.IO) {
        dao.getNewEssentialWords(limit)
    }

    suspend fun getEssentialWordsForUnit(book: String, unit: String): List<EssentialWord> = withContext(Dispatchers.IO) {
        dao.getEssentialWordsForUnit(book, unit)
    }

    suspend fun markEssentialWordsAsLearned(words: List<EssentialWord>) = withContext(Dispatchers.IO) {
        val time = System.currentTimeMillis()
        val progresses = words.map { 
            // set next review to 1 day from now as interval start
            EssentialProgress(it.id, true, time, time + 86400000L, 1) 
        }
        dao.insertEssentialProgress(progresses)
    }

    suspend fun updateEssentialProgress(progress: EssentialProgress) = withContext(Dispatchers.IO) {
        dao.insertEssentialProgress(progress)
    }

    suspend fun getEssentialProgress(wordId: Int): EssentialProgress? = withContext(Dispatchers.IO) {
        dao.getEssentialProgress(wordId)
    }

    suspend fun getEssentialWordsDue(limit: Int): List<EssentialWord> = withContext(Dispatchers.IO) {
        dao.getEssentialWordsDue(System.currentTimeMillis(), limit)
    }

    suspend fun getEssentialMistakeWords(limit: Int): List<EssentialWord> = withContext(Dispatchers.IO) {
        dao.getEssentialMistakeWords(limit)
    }

    suspend fun reportEssentialMistake(wordId: Int) = withContext(Dispatchers.IO) {
        val existing = dao.getEssentialMistake(wordId)
        if (existing != null) {
            dao.incrementEssentialMistake(wordId)
        } else {
            dao.insertEssentialMistake(EssentialMistake(wordId, 1))
        }
    }

    suspend fun getRandomEssentialOptions(excludeId: Int, limit: Int): List<EssentialWord> = withContext(Dispatchers.IO) {
        dao.getRandomEssentialOptions(excludeId, limit)
    }

    // NEW SEARCH & STATS WRAPPERS
    suspend fun searchWords(query: String): List<Word> = withContext(Dispatchers.IO) {
        dao.searchWords(query)
    }

    suspend fun searchEssentialWords(query: String): List<EssentialWord> = withContext(Dispatchers.IO) {
        dao.searchEssentialWords(query)
    }

    suspend fun getWordStats(): Map<String, Int> = withContext(Dispatchers.IO) {
        mapOf(
            "total" to dao.getWordCount(),
            "learned" to dao.getLearnedWordCount(),
            "mistakes" to dao.getTotalMistakeCount()
        )
    }

    suspend fun getEssentialStats(): Map<String, Int> = withContext(Dispatchers.IO) {
        mapOf(
            "total" to dao.getEssentialWordCount(),
            "learned" to dao.getLearnedEssentialWordCount(),
            "mistakes" to dao.getTotalEssentialMistakeCount()
        )
    }
}
