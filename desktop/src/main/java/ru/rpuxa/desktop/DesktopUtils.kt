package ru.rpuxa.desktop

import java.io.File

object DesktopUtils {
    fun getResource(file: String) = File(ClassLoader.getSystemClassLoader().getResource(file).file)
}