package ru.rpuxa.wirelessadb.core

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.ServerSocket

object Server {

    @Synchronized
    fun openServerSocket() {
        if (isVisible)
            return
        isVisible = true
        Thread {
            while (isVisible) {
                val ip = InetAddress.getByName(getIp())
                val port = ip.myPort
                while (serverSocket == null) {
                    try {
                        serverSocket = ServerSocket(port, 0, ip)
                    } catch (e: Exception) {
                    }
                }

                while (isVisible) {
                    val socket = serverSocket!!.accept()
                    val input = socket.getInputStream()
                    val output = socket.getOutputStream()

                    Device(socket, ObjectOutputStream(output), ObjectInputStream(input))
                }
            }
        }.start()
    }

    @Synchronized
    fun closeServerSocket() {
        if (!isVisible)
            return
        if (serverSocket != null)
            serverSocket!!.close()
        if (input != null)
            input!!.close()
    }

    private var isVisible = false
    private var serverSocket: ServerSocket? = null
    private var input: InputStream? = null
}