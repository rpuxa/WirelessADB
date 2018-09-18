package ru.rpuxa.desktop.main

import ru.rpuxa.desktop.DesktopActions
import ru.rpuxa.desktop.visual.MainPanel
import javax.swing.JFrame
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    val frame = JFrame()
    frame.add(MainPanel(DesktopActions))
    frame.setSize(600, 600)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.isVisible = true
}