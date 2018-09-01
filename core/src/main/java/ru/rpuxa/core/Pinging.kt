package ru.rpuxa.core

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.Socket

internal object Pinging {

    internal var ping = false
        set(value) {
            if (value)
                startPingDevices()
            else
                stopPingDevices()
            field = value
        }


    @Synchronized
    private fun startPingDevices() {
        if (pingingDevices || CoreServer.deviceInfo.isMobile)
            return
        pingingDevices = true

        Thread {
            var ip: String? = null

            while (ip == null) {
                ip = getIp()
                if (!pingingDevices)
                    return@Thread
            }


            val address = InetAddress.getByName(ip).address

            for (lastByte in 1..254) {
                Thread {
                    val newAddress = address.clone()
                    newAddress[3] = lastByte.toByte()
                    while (pingingDevices) {
                        val byAddress = InetAddress.getByAddress(newAddress)
                        if (byAddress.isReachable(1000))
                            checkDevice(byAddress)
                    }
                }.start()
            }
        }.start()
    }

    @Synchronized
    private fun stopPingDevices() {
        pingingDevices = false
    }

    private var pingingDevices = false

    private fun checkDevice(address: InetAddress) {
        try {
            val port = address.myPort
            if (port != InetAddress.getByName(getIp()).myPort) {
                val socket = Socket(address, port)
                val output = socket.getOutputStream()
                val input = socket.getInputStream()

                DeviceConnection(socket, ObjectOutputStream(output), ObjectInputStream(input), address)
            }
        } catch (e: Throwable) {
        }
    }

}