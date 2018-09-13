package ru.rpuxa.internalServer.main

import ru.rpuxa.core.trd
import ru.rpuxa.internalServer.InternalServer
import ru.rpuxa.pc.desktop.DesktopActions
import ru.rpuxa.pc.visual.MainPanel
import javax.swing.JFrame
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    if (args.size == 1) {
        when {
            args[0] == "server" -> {
                runServer()
            }
            args[0] == "desktop" -> {
                runDesktop()
            }
            args[0] == "all" -> {
                trd { runServer() }
                runDesktop()
            }
        }
    } else
        throw IllegalStateException("Wrong arguments")
}

private fun runDesktop() {
    val frame = JFrame()
    frame.add(MainPanel(DesktopActions))
    frame.setSize(600, 600)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.isVisible = true
}

private fun runServer() {
    InternalServer.init()
}

