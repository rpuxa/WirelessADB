package ru.rpuxa.wirelessadb.core

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

var ADB = "C:\\Programs\\SDK\\oldSdk\\SDK\\platform-tools"

fun startADB(ip: InetAddress) =
        try {
            val address = "${ip.toString().substring(1)}:5555"
            val builder = ProcessBuilder("cmd.exe", "/c", "cd $ADB && adb connect $address")
            builder.redirectErrorStream(true)
            builder.start()
            true
        } catch (e: Throwable) {
            false
        }


