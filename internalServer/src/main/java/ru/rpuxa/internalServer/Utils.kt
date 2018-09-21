package ru.rpuxa.internalServer

import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList


internal fun getIp(): String? {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces().reverse()
    for (network in networkInterfaces) {
        for (address in network.inetAddresses) {
            if (address.toString().startsWith("/192.168."))
                return address.toString().substring(1)
        }
    }

    return null
}

internal fun <T> Enumeration<T>.reverse(): List<T> {
    val list = ArrayList<T>()
    for (e in this) {
        list.add(e)
    }
    list.reverse()
    return list
}

internal val InetAddress.myPort
    get() = address[3] + 31812
