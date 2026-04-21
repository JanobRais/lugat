package com.lugat.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lugat.app.data.entity.Mistake
import com.lugat.app.data.entity.Progress
import com.lugat.app.data.entity.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface LugatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    // Get N unlearned words ordered sequentially by id
    @Query("""
        SELECT * FROM words 
        WHERE id NOT IN (SELECT wordId FROM progress)
        ORDER BY id ASC LIMIT :limit
    """)
    suspend fun getNewWords(limit: Int): List<Word>

    // Mark as learned
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: Progress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: List<Progress>)

    // Mistakes tracking
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: Mistake)

    @Query("SELECT * FROM mistakes WHERE wordId = :wordId")
    suspend fun getMistake(wordId: Int): Mistake?

    @Query("UPDATE mistakes SET mistakeCount = mistakeCount + 1 WHERE wordId = :wordId")
    suspend fun incrementMistake(wordId: Int)

    // Get words that have mistakes
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN mistakes m ON w.id = m.wordId
        WHERE m.mistakeCount > 0
        ORDER BY RANDOM() LIMIT :limit
    """)
    suspend fun getMistakeWords(limit: Int): List<Word>

    // Get learned words for mixed test
    @Query("""
        SELECT w.* FROM words w
        INNER JOIN progress p ON w.id = p.wordId
        ORDER BY RANDOM() LIMIT :limit
    """)
    suspend fun getOldLearnedWords(limit: Int): List<Word>

    // Get random words for options
    @Query("SELECT * FROM words WHERE id != :excludeId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomOptions(excludeId: Int, limit: Int): List<Word>

    // ----------------------------------------------------
    // ESSENTIAL 4000
    // ----------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEssentialWords(words: List<com.lugat.app.data.entity.EssentialWord>)

    @Query("SELECT COUNT(*) FROM essential_words")
    suspend fun getEssentialWordCount(): Int

    @Query("SELECT DISTINCT bookName FROM essential_words ORDER BY bookName ASC")
    suspend fun getEssentialBooks(): List<String>

    @Query("SELECT DISTINCT unitName FROM essential_words WHERE bookName = :book ORDER BY unitName ASC")
    suspend fun getEssentialUnitsForBook(book: String): List<String>

    // Get words for a given unit
    @Query("SELECT * FROM essential_words WHERE bookName = :book AND unitName = :unit ORDER BY id ASC")
    suspend fun getEssentialWordsForUnit(book: String, unit: String): List<com.lugat.app.data.entity.EssentialWord>

    // Mark as learned/scheduled
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEssentialProgress(progress: com.lugat.app.data.entity.EssentialProgress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEssentialProgress(progresses: List<com.lugat.app.data.entity.EssentialProgress>)

    @Query("SELECT * FROM essential_progress WHERE wordId = :wordId")
    suspend fun getEssentialProgress(wordId: Int): com.lugat.app.data.entity.EssentialProgress?

    // Mistakes tracking
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEssentialMistake(mistake: com.lugat.app.data.entity.EssentialMistake)

    @Query("SELECT * FROM essential_mistakes WHERE wordId = :wordId")
    suspend fun getEssentialMistake(wordId: Int): com.lugat.app.data.entity.EssentialMistake?

    @Query("UPDATE essential_mistakes SET mistakeCount = mistakeCount + 1 WHERE wordId = :wordId")
    suspend fun incrementEssentialMistake(wordId: Int)

    // Option words for Essential tests
    @Query("SELECT * FROM essential_words WHERE id != :excludeId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomEssentialOptions(excludeId: Int, limit: Int): List<com.lugat.app.data.entity.EssentialWord>

    // Words due for review (Spaced Repetition)
    @Query("""
        SELECT w.* FROM essential_words w
        INNER JOIN essential_progress p ON w.id = p.wordId
        WHERE p.nextReviewDate > 0 AND p.nextReviewDate <= :currentTime
        ORDER BY p.nextReviewDate ASC LIMIT :limit
    """)
    suspend fun getEssentialWordsDue(currentTime: Long, limit: Int): List<com.lugat.app.data.entity.EssentialWord>
}
