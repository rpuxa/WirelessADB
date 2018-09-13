package ru.rpuxa.internalServer

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import ru.rpuxa.core.trd
import ru.rpuxa.internalServer.InternalServer.info
import java.io.IOException
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
        if (pingingDevices || info.isMobile)
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

            for (lastByte in 1..254)
                trd {
                    while (pingingDevices) {
                        val newAddress = address.clone()
                        newAddress[3] = lastByte.toByte()
                        val byAddress = InetAddress.getByAddress(newAddress)
                        if (byAddress.isReachable(1000))
                            checkDevice(byAddress)
                        Thread.sleep(500)
                    }
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
                println(address)
                val socket = Socket(address, port)
                val output = socket.getOutputStream()
                val input = socket.getInputStream()
                DeviceConnection(socket, ObjectOutputStream(output), ObjectInputStream(input), address)
            }
        } catch (e: IOException) {
        }
    }

}

fun main(args: Array<String>) {
    for (i in 1..1000)
        launch {
            println(i)
            delay(500)
            println(i)
        }
    Thread.sleep(1000)
}


