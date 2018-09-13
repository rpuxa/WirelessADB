package ru.rpuxa.internalServer

import ru.rpuxa.core.containsAdb
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress

const val UNKNOWN_ERROR = -103

val ADB: String
    get() = InternalServer.info.adbPath

internal fun changeADB(ip: InetAddress, connect: Boolean = true) =
        try {
            if (!File(ADB).containsAdb)
                throw IOException()
            val address = "${ip.toString().substring(1)}:5555"
            val builder = ProcessBuilder("cmd.exe", "/c", "cd $ADB && adb ${if (connect) "" else "dis"}connect $address")
            builder.redirectErrorStream(true)
            val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
            var fullAnswer = ""
            while (true) {
                val line = reader.readLine() ?: break
                fullAnswer += line
                println(line)
            }
            val openBracket = fullAnswer.lastIndexOf('(')
            val closeBracket = fullAnswer.lastIndexOf(')')
            if (openBracket != -1)
                fullAnswer.substring(openBracket + 1, closeBracket).toInt()
            else
                0
        } catch (e: IOException) {
            UNKNOWN_ERROR
        }

internal fun checkADB(ip: InetAddress): Boolean {
    try {
        val address = "${ip.toString().substring(1)}:5555"
        val builder = ProcessBuilder("cmd.exe", "/c", "cd $ADB && adb devices")
        val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
        while (true) {
            val line = reader.readLine() ?: return false
            println(line)
            if (line.contains(address)) {
                println("found! adbcheck")
                return true
            }
        }
    } catch (e: IOException) {
        return false
    }
}

fun fixAdb10061(ip: InetAddress) =
        try {
            println("Fixing 10061...")
            val builder = ProcessBuilder(
                    "cmd.exe",
                    "/c",
                    "cd $ADB && adb usb && adb kill-server && adb tcpip 5555 && adb connect ${ip.toString().substring(1)}:5555"
            )
            val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
            while (true) {
                val line = reader.readLine() ?: break
                println(line)
            }
            checkADB(ip)
        } catch (e: IOException) {
            false
        }
