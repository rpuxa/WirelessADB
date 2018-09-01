package ru.rpuxa.core

import ru.rpuxa.core.CoreServer.deviceInfo
import ru.rpuxa.core.CoreServer.devices
import ru.rpuxa.core.listeners.DeviceListener
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

internal class DeviceConnection(
        private val socket: Socket,
        private val output: ObjectOutputStream,
        private val input: ObjectInputStream,
        internal val ip: InetAddress
) {

    internal var id = -1L
    private var name = ""
    private var isMobile = false

    internal var listener: DeviceListener? = null
    private var connected = false

    init {
        try {
            sendMessage(ID, deviceInfo.id!!)
            sendMessage(NAME, deviceInfo.settings.deviceName)
            sendMessage(TYPE, deviceInfo.isMobile)

            val connected = AtomicBoolean(false)
            Thread {
                Thread.sleep(5000)
                if (!connected.get()) {
                    output.close()
                    input.close()
                }
            }.start()
            repeat(3) {
                val message = readMessage()
                when (message.command) {
                    ID -> {
                        id = message.data as Long
                    }

                    NAME -> {
                        name = message.data as String
                    }

                    TYPE -> {
                        isMobile = message.data as Boolean
                    }
                }

            }
            if (devices.find { it.id == id } == null) {
                devices.add(this)
                connected.set(true)
                this.connected = true
                Thread {
                    while (true) {
                        Thread.sleep(5000)
                        if (!sendMessage(CHECK))
                            break
                    }
                    disconnect()
                }.start()
                startListening()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onDisconnected()
        }
    }

    private fun readMessage() = input.readObject() as Message

    internal fun sendMessage(command: Int, data: Any? = null) = try {
        output.writeObject(Message(command, data))
        output.flush()
        true
    } catch (e: Throwable) {
        onDisconnected()
        false
    }

    private fun startListening() {
        try {
            while (connected) {
                val message = readMessage()
                onMessage(message)
                if (listener != null)
                    listener!!.onGetMessage(message)
            }
        } catch (e: Exception) {
            onDisconnected()
        }
    }

    private fun onMessage(msg: Message) {
        when (msg.command) {
            CONNECT_ADB -> {
                if (isMobile && !CoreServer.deviceInfo.isMobile) {
                    val res = changeADB(ip)
                    if (res == 0)
                        sendMessage(ADB_OK)
                    else
                        sendMessage(ADB_ERROR, res)
                } else
                    sendMessage(ADB_FAIL)
            }

            ADB_CHECK -> {
                if (isMobile && !CoreServer.deviceInfo.isMobile && checkADB(ip)) {
                    sendMessage(ADB_OK)
                } else
                    sendMessage(ADB_FAIL)
            }

            DISCONNECT_ADB -> {
                if (isMobile && !CoreServer.deviceInfo.isMobile && changeADB(ip, false) == 0) {
                    sendMessage(ADB_OK)
                } else
                    sendMessage(ADB_FAIL)
            }
        }
    }

    internal fun disconnect() {
        if (!connected)
            return
        try {
            socket.close()
        } catch (e: Exception) {
        }
        try {
            input.close()
        } catch (e: Exception) {
        }
        try {
            output.close()
        } catch (e: Exception) {
        }
        connected = false
        onDisconnected()
    }

    internal val serializable: Device
        get() = Device(id, name, isMobile)

    private fun onDisconnected() {
        devices.remove(this)
        if (listener != null)
            listener!!.onDisconnected()
    }

    internal fun sendMessageAndWaitResponse(command: Int, data: Any? = null): Message? {
        var answer: Message? = null
        var disconnect = false
        listener = object : DeviceListener {
            override fun onGetMessage(message: Message) {
                answer = message
            }

            override fun onDisconnected() {
                disconnect = true
            }
        }
        sendMessage(command, data)

        while (answer == null && !disconnect)
            Thread.sleep(10)
        listener = null
        if (disconnect)
            return null
        return answer
    }


}

