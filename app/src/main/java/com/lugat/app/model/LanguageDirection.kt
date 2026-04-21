package com.lugat.app.model

import com.lugat.app.data.entity.Word

enum class LanguageDirection(val displayName: String) {
    RU_UZ("Russian → Uzbek"),
    UZ_RU("Uzbek → Russian"),
    EN_RU("English → Russian"),
    RU_EN("Russian → English"),
    UZ_EN("Uzbek → English"),
    EN_UZ("English → Uzbek");

    fun getSourceText(word: Word): String {
        return when (this) {
            RU_UZ, RU_EN -> word.ru
            UZ_RU, UZ_EN -> word.uz
            EN_RU, EN_UZ -> word.en
        }
    }

    fun getTargetText(word: Word): String {
        return when (this) {
            UZ_RU, EN_RU -> word.ru
            RU_UZ, EN_UZ -> word.uz
            RU_EN, UZ_EN -> word.en
        }
    }
}
