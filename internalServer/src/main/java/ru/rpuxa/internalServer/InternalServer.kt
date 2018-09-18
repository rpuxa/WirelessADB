package ru.rpuxa.internalServer

import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.DeviceInfoSerializable
import ru.rpuxa.core.internalServer.Message
import ru.rpuxa.core.internalServer.SET_DEVICE_INFO
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.ServerSocket

object InternalServer {

    internal val devices = ArrayList<DeviceConnection>()

    internal lateinit var info: DeviceInfoSerializable

    fun init() {
        val serverSocket = ServerSocket(7158, 0, InetAddress.getByName("localhost"))
        while (true) {
            val socket = serverSocket.accept()
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()
            val input = ObjectInputStream(inputStream)
            val output = ObjectOutputStream(outputStream)
            val message = input.readObject() as Message
            var close = false
            if (onMessage(message, output))
                close = true
            try {
                input.close()
            } catch (e: Exception) {
            }
            try {
                output.close()
            } catch (e: Exception) {
            }
            try {
                socket.close()
            } catch (e: Exception) {
            }
            if (close) {
                serverSocket.close()
                System.exit(0)
            }
        }
    }

    private fun onMessage(msg: Message, output: ObjectOutputStream): Boolean {
        fun sendMessage(any: Any) {
            try {
                output.writeObject(any)
                output.flush()
            } catch (e: Exception) {
            }
        }
        when (msg.command) {
            CHECK -> {
                sendMessage(EMPTY_MESSAGE)
            }
            GET_DEVICE_LIST -> {
                val devices = devices.toTypedArray()
                val list = Array(devices.size) {
                    devices[it].serializable
                }
                sendMessage(list)
            }

            CLOSE_SERVER -> {
                sendMessage(EMPTY_MESSAGE)
                Pinging.ping = false
                Server.closeServerSocket()
                return true
            }

            CONNECT_ADB -> {
                val device = (msg.data as Device).getDeviceConnection {
                    sendMessage(Message(ADB_FAIL))
                    return false
                }
                if (info.isMobile) {
                    val answer = device.sendMessageAndWaitResponse(CONNECT_ADB)!!
                    when (answer.command) {
                        ADB_FAIL -> sendMessage(Message(ADB_FAIL))
                        ADB_ERROR -> sendMessage(answer)
                        else -> sendMessage(Message(ADB_OK))
                    }
                    return false
                } else {
                    val res = changeADB(device.ip)
                    if (res == 0)
                        sendMessage(Message(ADB_OK, null))
                    else
                        sendMessage(Message(ADB_ERROR, res))
                }
            }

            ADB_CHECK -> {
                val device = (msg.data as Device).getDeviceConnection {
                    sendMessage(ADB_FAIL)
                    return false
                }
                if (info.isMobile) {
                    val answer = device.sendMessageAndWaitResponse(ADB_CHECK)
                    if (answer == null || answer.command == ADB_FAIL) {
                        sendMessage(ADB_FAIL)
                        return false
                    }
                    sendMessage(ADB_OK)
                } else {
                    sendMessage(if (checkADB(device.ip)) ADB_OK else ADB_FAIL)

                }
            }

            DISCONNECT_ADB -> {
                val sDevice = msg.data as Device
                val device = devices.find { it.id == sDevice.id }
                if (device != null) {
                    if (info.isMobile) {
                        device.sendMessageAndWaitResponse(DISCONNECT_ADB)
                    } else
                        changeADB(device.ip, false)
                }
                sendMessage(EMPTY_MESSAGE)
            }

            FIX_ADB_10061 -> {
                val device = (msg.data as Device).getDeviceConnection {
                    sendMessage(ADB_FAIL)
                    return false
                }
                if (info.isMobile) {
                    val answer = device.sendMessageAndWaitResponse(FIX_ADB_10061)
                    if (answer == null || answer.command == ADB_FAIL) {
                        sendMessage(ADB_FAIL)
                        return false
                    }
                    sendMessage(ADB_OK)
                } else
                    sendMessage(if (fixAdb10061(device.ip)) ADB_OK else ADB_FAIL)
            }

            SET_DEVICE_INFO -> {
                info = msg.data as DeviceInfoSerializable
                Server.openServerSocket()
                Pinging.ping = true
            }
        }
        return false
    }

    private inline fun Device.getDeviceConnection(onNotFound: () -> Unit): DeviceConnection {
        val device = devices.find { it.id == id }
        if (device == null) {
            onNotFound()
            throw IllegalStateException("Return missed")
        }
        return device
    }
}