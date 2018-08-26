package ru.rpuxa.pc

import javax.swing.JComponent

interface Actions {

    fun sendMessage(msg: String, panel: JComponent)
}