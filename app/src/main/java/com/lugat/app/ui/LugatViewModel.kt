package com.lugat.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        viewModelScope.launch {
            repository.checkAndPopulateDatabase()
            _isDbInitialized.value = true
        }
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
}
