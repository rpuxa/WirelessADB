package ru.rpuxa.internalServer

import ru.rpuxa.internalServer.InternalServer.info
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.concurrent.thread

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
        if (pingingDevices || info.isMobile)
            return
        pingingDevices = true


        thread(isDaemon = true) daemon@{
            var ip: String? = null

            while (ip == null) {
                ip = getIp()
                if (!pingingDevices)
                    return@daemon
            }


            val address = InetAddress.getByName(ip).address

            for (lastByte in 1..254)
                thread {
                    while (pingingDevices) {
                        val newAddress = address.clone()
                        newAddress[3] = lastByte.toByte()
                        val byAddress = InetAddress.getByAddress(newAddress)
                        if (byAddress.isReachable(1000))
                            checkDevice(byAddress)
                        Thread.sleep(500)
                    }
                }
        }
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
                val socket = Socket()
                socket.connect(InetSocketAddress(address, port), 500)
                val output = socket.getOutputStream()
                val input = socket.getInputStream()
                DeviceConnection(socket, ObjectOutputStream(output), ObjectInputStream(input), address)
            }
        } catch (e: IOException) {
        }
    }

}