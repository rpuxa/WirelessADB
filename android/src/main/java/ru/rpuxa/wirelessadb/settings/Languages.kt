package ru.rpuxa.wirelessadb.settings

import java.io.Serializable

enum class Languages(val id: Int, val languageName: String, val string: String) : Serializable {
    ENGLISH(0, "English", "english"),
    RUSSIAN(1, "Русский", "russian")
}