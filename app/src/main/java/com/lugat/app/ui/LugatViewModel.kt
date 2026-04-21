package com.lugat.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lugat.app.data.entity.EssentialMistake
import com.lugat.app.data.entity.EssentialProgress
import com.lugat.app.data.entity.EssentialWord
import com.lugat.app.data.entity.Word
import com.lugat.app.model.LanguageDirection
import com.lugat.app.repository.LugatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LugatViewModel @Inject constructor(
    private val repository: LugatRepository
) : ViewModel() {

    private val _isDbInitialized = MutableStateFlow(false)
    val isDbInitialized: StateFlow<Boolean> = _isDbInitialized.asStateFlow()

    private val _dailySettings = MutableStateFlow(
        Pair(repository.dailyWordLimit, repository.languageDirection)
    )
    val dailySettings = _dailySettings.asStateFlow()

    private val _activeDictionary = MutableStateFlow(repository.activeDictionaryType)
    val activeDictionary = _activeDictionary.asStateFlow()

    val streakCount = repository.streakCount

    init {
        viewModelScope.launch {
            repository.checkAndPopulateDatabase()
            repository.checkAndPopulateEssentialDatabase()
            
            // Streak computation:
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val lastLogin = repository.lastLoginDate
            if (lastLogin != today) {
                // If it wasn't today, check if it was yesterday
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                val yesterday = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time)
                
                if (lastLogin != yesterday) {
                    // Streak broken (or first time)
                    repository.streakCount = 0
                }
                
                // We don't increment streak here immediately; we increment it when a learning session finishes.
                // Or we can increment it when they do their first session of the day.
            }
            
            _isDbInitialized.value = true
        }
    }

    fun markSessionCompleted() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        if (repository.lastLoginDate != today) {
            repository.lastLoginDate = today
            repository.streakCount += 1
        }
    }

    fun switchDictionary(type: String) {
        repository.activeDictionaryType = type
        _activeDictionary.value = type
    }

    fun updateSettings(limit: Int, direction: LanguageDirection) {
        repository.dailyWordLimit = limit
        repository.languageDirection = direction
        _dailySettings.value = Pair(limit, direction)
    }

    // A unified logic class for Test / Learn flows
    suspend fun getDailyWords(): List<Word> = repository.getDailyWords()
    
    suspend fun getMistakeWords(limit: Int): List<Word> = repository.getMistakeWords(limit)
    
    suspend fun getOldLearnedWords(limit: Int): List<Word> = repository.getOldLearnedWords(limit)

    suspend fun markWordsAsLearned(words: List<Word>) = repository.markWordsAsLearned(words)

    suspend fun reportMistake(wordId: Int) = repository.reportMistake(wordId)

    suspend fun getRandomOptions(excludeId: Int, limit: Int): List<Word> = repository.getRandomOptions(excludeId, limit)

    // Essential Methods
    suspend fun getEssentialBooks(): List<String> = repository.getEssentialBooks()
    suspend fun getEssentialUnitsForBook(book: String): List<String> = repository.getEssentialUnitsForBook(book)
    suspend fun getEssentialWordsForUnit(book: String, unit: String) = repository.getEssentialWordsForUnit(book, unit)
    suspend fun markEssentialWordsAsLearned(words: List<EssentialWord>) = repository.markEssentialWordsAsLearned(words)
    suspend fun updateEssentialProgress(progress: EssentialProgress) = repository.updateEssentialProgress(progress)
    suspend fun getEssentialProgress(wordId: Int) = repository.getEssentialProgress(wordId)
    suspend fun getEssentialWordsDue(limit: Int) = repository.getEssentialWordsDue(limit)
    suspend fun getEssentialMistakeWords(limit: Int) = repository.getEssentialMistakeWords(limit)
    suspend fun reportEssentialMistake(wordId: Int) = repository.reportEssentialMistake(wordId)
    suspend fun getRandomEssentialOptions(excludeId: Int, limit: Int): List<EssentialWord> {
        return repository.getRandomEssentialOptions(excludeId, limit)
    }

    // SEARCH & STATS
    suspend fun searchWords(query: String): List<Word> = repository.searchWords(query)
    suspend fun searchEssentialWords(query: String): List<EssentialWord> = repository.searchEssentialWords(query)
    
    suspend fun getWordStats() = repository.getWordStats()
    suspend fun getEssentialStats() = repository.getEssentialStats()
}
