package ru.rpuxa.wirelessadb.core

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket


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
        this.deviceInfo = deviceInfo
        val thread = Thread(::init)
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
     */
    fun connectAdb(device: SerializableDevice) {
        TODO("Сделать подключение к ADB")
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
                println("Отправили сообщение")
                input.readObject()
            } catch (e: Exception) {
                null
            }


    internal lateinit var deviceInfo: ThisDeviceInfo
    internal val devices = ArrayList<Device>()

    private fun init() {
        Server.openServerSocket()
        Pinging.ping = true
        val serverSocket = ServerSocket(7158, 0, InetAddress.getByName("localhost"))
        while (true) {
            val socket = serverSocket.accept()
            val inputStream = socket.getInputStream()
            val outputStream = socket.getOutputStream()
            val input = ObjectInputStream(inputStream)
            val output = ObjectOutputStream(outputStream)
            println("Поулчили сообщение")
            val message = input.readObject() as Message
            var close = false
            if (onMessage(message, output))
                close = true
            input.close()
            output.close()
            socket.close()
            if (close) {
                serverSocket.close()
                return
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
                val devices = this.devices.toTypedArray()
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
        }
        return false
    }
}