package ru.rpuxa.desktop

import java.io.File

fun getResource(file: String) = File(ClassLoader.getSystemClassLoader().getResource(file).file)
