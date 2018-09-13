package ru.rpuxa.core

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * Запускает поток
 *
 * Usage:
 * trd {
 * //code
 * }
 */
inline fun trd(crossinline block: () -> Unit) =
        Thread {
            block()
        }.start()


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