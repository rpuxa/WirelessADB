package ru.rpuxa.core

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.ServerSocket

internal object Server {

    @Synchronized
    internal fun openServerSocket() {
        if (isVisible)
            return
        isVisible = true
        Thread {
            try {
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

                        Device(socket, ObjectOutputStream(output), ObjectInputStream(input), socket.inetAddress)
                    }
                }
            } catch (e: Throwable) {
            }
        }.start()
    }

    @Synchronized
    internal fun closeServerSocket() {
        if (!isVisible)
            return
        isVisible = false
        try {
            if (serverSocket != null)
                serverSocket!!.close()
        } catch (e: Exception) {
        }
        try {
            if (input != null)
                input!!.close()
        } catch (e: Exception) {
        }
        serverSocket = null
    }

    private var isVisible = false
    private var serverSocket: ServerSocket? = null
    private var input: InputStream? = null
}