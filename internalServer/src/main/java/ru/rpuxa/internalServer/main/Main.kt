package ru.rpuxa.internalServer.main

import ru.rpuxa.internalServer.InternalServer
import ru.rpuxa.pc.desktop.DesktopActions
import ru.rpuxa.pc.visual.MainPanel
import javax.swing.JFrame
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    if (args.size == 1) {
        if (args[0] == "server") {
            runServer()
            return
        } else if (args[0] == "desktop") {
            runDesktop()
            return
        }
    }
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

