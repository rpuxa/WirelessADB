package ru.rpuxa.core

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

val File.containsAdb: Boolean
    get() {
        try {
            val builder = ProcessBuilder("cmd.exe", "/c", "cd $this && adb version")
            builder.redirectErrorStream(true)
            val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
            while (true) {
                val line = reader.readLine() ?: return false
                if (line.startsWith("Android Debug Bridge version"))
                    return true
            }
        } catch (e: IOException) {
            return false
        }
    }

fun checkName(name: String): Boolean {
    if (name.length < 4 || name.length > 16)
        return false
    for (char in name) {
        if (char !in 'a'..'z' && char !in 'A'..'Z' && char !in 'а'..'я' &&
                char !in 'А'..'Я' && char !in '0'..'9' && char != '-' && char != '_')
            return false
    }
    return true
}
