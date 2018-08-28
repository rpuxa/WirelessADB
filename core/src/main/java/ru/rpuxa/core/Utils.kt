package ru.rpuxa.core

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.NetworkInterface


internal fun getIp(): String? {
    for (network in NetworkInterface.getNetworkInterfaces()) {
        for (address in network.inetAddresses) {
            if (address.toString().startsWith("/192.168."))
                return address.toString().substring(1)
        }
    }

    return null
}

internal val InetAddress.myPort
    get() = address[3] + 31812

var ADB = "C:\\Programs\\SDK\\platform-tools"

internal fun changeADB(ip: InetAddress, connect: Boolean = true) =
        try {
            val address = "${ip.toString().substring(1)}:5555"
            val builder = ProcessBuilder("cmd.exe", "/c", "cd $ADB && adb ${if (connect) "" else "dis"}connect $address")
            builder.redirectErrorStream(true)
            builder.start()
            checkADB(ip)
        } catch (e: Throwable) {
            false
        }

internal fun checkADB(ip: InetAddress): Boolean {
    try {
        val address = "${ip.toString().substring(1)}:5555"
        val builder = ProcessBuilder("cmd.exe", "/c", "cd $ADB && adb devices")
        val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
        while (true) {
            val line = reader.readLine() ?: return false
            if (line.contains(address))
                return true
        }
    } catch (e: Throwable) {
        return false
    }
}

fun Array<Device>.equalsElements(arr: Array<Device>): Boolean {
    for (element in this) {
        if (arr.find { it.id == element.id } == null) {
            return false
        }
    }

    return true
}

