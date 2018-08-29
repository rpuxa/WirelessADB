package ru.rpuxa.wirelessadb.settings

import java.io.Serializable

enum class Languages(val id: Int, val languageName: String) : Serializable {
    ENGLISH(0, "English"),
    RUSSIAN(1, "Русский")
}