package ru.rpuxa.internalServer

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
