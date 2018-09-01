package ru.rpuxa.core

import ru.rpuxa.core.listeners.AdbListener
import ru.rpuxa.core.listeners.ServerListener
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.*
import java.util.concurrent.ConcurrentHashMap


/**
 * Главный класс где можно получить информацию об устройствах итд
 */
object CoreServer {


    /**
     *  Включить видимость и начать поиск устройств в Wifi сети
     */
    fun startServer(info: ThisDeviceInfo, listener: ServerListener) =
            trd {
                if (isAvailable)
                    return@trd
                deviceListener = listener
                deviceInfo = info
                val thread = Thread(CoreServer::init)
                thread.isDaemon = true
                thread.start()
            }

    private var deviceListener: ServerListener? = null


    /**
     * Выключить @see[startServer]
     */
    fun closeServer() {
        sendMessageToServer(CLOSE_CORE_SERVER)
        devices.toTypedArray().forEach { it.disconnect() }
        devices.clear()
    }

    /**
     * Включена ли видимость @see[startServer]
     */
    val isAvailable: Boolean
        get() = sendMessageToServer(CHECK) != null


    /**
     *  Получить массив устройств @see[Device]
     *
     *  returns null - если сервер не включен @see[startServer]
     */
    @Suppress("UNCHECKED_CAST")
    fun getDevicesList() = sendMessageToServer(GET_DEVICE_LIST) as Array<Device>?


    /**
     * Подключить ADB к данному устройству
     *
     */
    fun connectAdb(device: Device, listener: AdbListener) {
        trd {
            val msg = sendMessageToServer(CONNECT_ADB, device) as Message
            if (msg.command == ADB_OK) {
                listener.onConnect()
            }

            if (msg.command == ADB_ERROR) {
                listener.onDisconnect()
                listener.onError(msg.data as Int)
            } else {
                startCheckingAdb(device, listener)
            }
        }
    }

    /**
     * Отключить адб
     */
    fun disconnectAdb(device: Device, listener: AdbListener) {
        trd {
            sendMessageToServer(DISCONNECT_ADB, device)
            startCheckingAdb(device, listener)
        }
    }

    /**
     * Проверить соединение adb с устройством [device]
     * true - если соединение поддерживается
     */
    @Synchronized
    fun checkAdb(device: Device) =
            sendMessageToServer(ADB_CHECK, device) == ADB_OK


    private val devicesThreads = ConcurrentHashMap<Device, Pair<Thread, AdbListener>>()

    private fun startCheckingAdb(device: Device, listener: AdbListener) {
        val pair = devicesThreads[device]
        if (pair != null) {
            devicesThreads[device] = pair.first to listener
            return
        }

        val thread = Thread {
            while (checkAdb(device))
                Thread.sleep(500)
            devicesThreads[device]!!.second.onDisconnect()
            devicesThreads.remove(device)
        }
        devicesThreads[device] = thread to listener
        thread.start()
    }

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

    internal lateinit var deviceInfo: ThisDeviceInfo

    internal val devices = object : ArrayList<DeviceConnection>() {
        override fun add(element: DeviceConnection): Boolean {
            if (deviceListener != null) {
                deviceListener!!.onAdd(element.serializable)
            }
            return super.add(element)
        }

        override fun remove(element: DeviceConnection): Boolean {
            if (deviceListener != null) {
                deviceListener!!.onRemove(element.serializable)
            }
            return super.remove(element)
        }
    }

    private fun init() {
        try {
            Server.openServerSocket()
            Pinging.ping = true
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
                    return
                }
            }
        } catch (e: BindException) {
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

            CLOSE_CORE_SERVER -> {
                sendMessage(EMPTY_MESSAGE)
                Pinging.ping = false
                Server.closeServerSocket()
                return true
            }

            CONNECT_ADB -> {
                val sDevice = msg.data as Device
                val device = devices.find { it.id == sDevice.id }
                if (device == null) {
                    sendMessage(Message(ADB_FAIL))
                    return false
                }
                if (deviceInfo.isMobile) {
                    val answer = device.sendMessageAndWaitResponse(CONNECT_ADB)
                    if (answer == null || answer.command == ADB_FAIL) {
                        sendMessage(Message(ADB_FAIL))
                        return false
                    }
                    sendMessage(Message(ADB_OK))
                } else {
                    val res = changeADB(device.ip)
                    if (res == 0)
                        sendMessage(Message(ADB_OK, null))
                    else
                        sendMessage(Message(ADB_ERROR, res))
                }
            }

            ADB_CHECK -> {
                val sDevice = msg.data as Device
                val device = devices.find { it.id == sDevice.id }
                if (device == null) {
                    sendMessage(ADB_FAIL)
                    return false
                }

                if (deviceInfo.isMobile) {
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
                    if (deviceInfo.isMobile) {
                        device.sendMessageAndWaitResponse(DISCONNECT_ADB)
                    } else
                        changeADB(device.ip, false)
                }
                sendMessage(EMPTY_MESSAGE)
            }
        }
        return false
    }
}