package ru.rpuxa.core.internalServer

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.concurrent.thread

object InternalServerController {

    /**
     *  Включить видимость и начать поиск устройств в Wifi сети
     */
    fun startServer(info: DeviceInfo, starter: Starter) =
            thread {
                if (!isAvailable) {
                    starter.startServer()
                    setDeviceInfo(info)
                }
            }

    fun setListener(listener: InternalServerListener) {
        this.listener = listener
        startListening()
    }


    /**
     * Выключить @see[startServer]
     */
    fun closeServer() =
            thread { sendMessageToServer(CLOSE_CORE_SERVER) }


    /**
     * Включена ли видимость @see[startServer]
     */
    val isAvailable: Boolean
        get() = sendMessageToServer(CHECK) != null

    /**
     * Подключить ADB к данному устройству
     *
     */
    fun connectAdb(device: Device) =
            thread {
                val msg = sendMessageToServer(CONNECT_ADB, device) as Message
                if (msg.command == ADB_ERROR && listener != null)
                    listener!!.onAdbError(device, msg.data as Int)
            }


    /**
     * Отключить адб
     */
    fun disconnectAdb(device: Device) =
            thread { sendMessageToServer(DISCONNECT_ADB, device) }


    /**
     * Проверить соединение adb с устройством [device]
     * true - если соединение поддерживается
     */
    fun checkAdb(device: Device) =
            sendMessageToServer(ADB_CHECK, device) == ADB_OK

    /**
     * Пофиксить ошибку 10061
     * true - если ошибка успешно пофиксилась (и это означает еще то, что адб
     * подключено)
     * Иначе не подключен провод или еще какая то ошибка
     */
    fun fixAdb10061(device: Device) =
            sendMessageToServer(FIX_ADB_10061, device) == ADB_OK

    fun setDeviceInfo(info: DeviceInfo) =
            sendMessageToServer(SET_DEVICE_INFO, DeviceInfoSerializable(info))


    interface Starter {

        fun startServer()
    }

    interface InternalServerListener {

        fun onServerConnected()

        fun onServerDisconnect()

        fun serverStillWorking()

        fun serverStillDisabled()

        fun onDeviceDetected(device: Device)

        fun onDeviceDisconnected(device: Device)

        fun onAdbConnected(device: Device)

        fun onAdbDisconnected(device: Device)

        fun onAdbError(device: Device, code: Int)
    }


    private var listener: InternalServerListener? = null


    private fun startListening() =
            thread {
                while (listener != null) {
                    val listener = listener!!
                    if (isAvailable) {
                        listener.onServerConnected()
                    } else {
                        listener.serverStillDisabled()
                        Thread.sleep(1000)
                        continue
                    }
                    while (isAvailable) {
                        listener.serverStillWorking()
                        val currentDevices = getDevicesList() ?: break
                        for (device in currentDevices) {
                            var findDevice = devices.find { it.device == device }
                            if (findDevice == null) {
                                listener.onDeviceDetected(device)
                                findDevice = AdbDevice(device, false)
                                devices.add(findDevice)
                            }
                            val adb = checkAdb(findDevice.device)
                            if (adb != findDevice.adb) {
                                if (adb) {
                                    listener.onAdbConnected(findDevice.device)
                                } else
                                    listener.onAdbDisconnected(findDevice.device)
                                findDevice.adb = adb

                            }
                        }
                        for (device in devices.toTypedArray()) {
                            if (currentDevices.find { it == device.device } == null) {
                                listener.onDeviceDisconnected(device.device)
                                for (i in devices.indices) {
                                    if (devices[i].device == device.device) {
                                        devices.removeAt(i)
                                        break
                                    }
                                }
                            }
                        }

                        Thread.sleep(1000)
                    }
                    listener.onServerDisconnect()
                }
            }


    private val devices = ArrayList<AdbDevice>()

    @Suppress("UNCHECKED_CAST")
    private fun getDevicesList() = sendMessageToServer(GET_DEVICE_LIST) as Array<Device>?

    @Synchronized
    private fun sendMessageToServer(command: Int, data: Any? = null) =
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("localhost", 7158), 100)
                val outputStream = socket.getOutputStream()
                val inputStream = socket.getInputStream()
                val output = ObjectOutputStream(outputStream)
                val input = ObjectInputStream(inputStream)
                output.writeObject(Message(command, data))
                output.flush()
                input.readObject()
            } catch (e: IOException) {
                null
            }

    private class AdbDevice(val device: Device, var adb: Boolean)
}