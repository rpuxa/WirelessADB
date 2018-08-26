package ru.rpuxa.core

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.*


/**
 * Главный класс где можно получить информацию об устройствах итд
 */
object CoreServer {

    /**
     *  Включить видимость и начать поиск устройств в Wifi сети
     */
    fun startServer(deviceInfo: ThisDeviceInfo) {
        if (isAvailable)
            return
        CoreServer.deviceInfo = deviceInfo
        val thread = Thread(CoreServer::init)
        thread.isDaemon = true
        thread.start()
    }

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
     *  Получить массив устройств @see[SerializableDevice]
     *
     *  returns null - если сервер не включен @see[startServer]
     */
    fun getDevicesList() = sendMessageToServer(GET_DEVICE_LIST) as Array<SerializableDevice>?


    /**
     * Подключить ADB к данному устройству
     *
     * reutrns true - если соединение прошло успешно
     */
    fun connectAdb(device: SerializableDevice) = sendMessageToServer(CONNECT_ADB, device) == ADB_OK


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
            } catch (e: Exception) {
                null
            }


    internal lateinit var deviceInfo: ThisDeviceInfo
    internal val devices = ArrayList<Device>()

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
                if (deviceInfo.isMobile) {
                    val sDevice = msg.data as SerializableDevice
                    val device = devices.find { it.id == sDevice.id }
                    if (device == null) {
                        sendMessage(ADB_FAIL)
                        return false
                    }
                    val answer = device.sendMessageAndWaitResponse(CONNECT_ADB)
                    if (answer == null || answer.command == ADB_FAIL) {
                        sendMessage(ADB_FAIL)
                        return false
                    }
                    sendMessage(ADB_OK)
                }
            }
        }
        return false
    }
}