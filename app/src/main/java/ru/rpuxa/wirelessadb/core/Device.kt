package ru.rpuxa.wirelessadb.core

import ru.rpuxa.wirelessadb.core.CoreServer.deviceInfo
import ru.rpuxa.wirelessadb.core.CoreServer.devices
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class Device(val socket: Socket, private val output: ObjectOutputStream, private val input: ObjectInputStream) {

    var id = -1L
    var name = ""
    var isMobile = false

    var listener: DeviceListener? = null
    var connected = false

    init {
        try {
            sendMessage(ID, deviceInfo.id!!)
            sendMessage(NAME, deviceInfo.name)
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
                connected.set(true)
                this.connected = true
                devices.add(this)
                Thread {
                    while (true) {
                        if (!socket.isClosed)
                            Thread.sleep(500)
                        else
                            break
                    }
                    disconnect()
                }.start()
            }
        } catch (e: Exception) {
            onDisconnected()
        }
    }

    fun readMessage() = input.readObject() as Message

    fun sendMessage(command: Int, data: Any) = try {
        output.writeObject(Message(command, data))
        output.flush()
        true
    } catch (e: Throwable) {
        onDisconnected()
        false
    }

    fun startListening() {
        try {
            while (connected) {
                val message = readMessage()
                if (listener != null)
                    listener!!.onGetMessage(message)
            }
        } catch (e: Exception) {
            onDisconnected()
        }
    }

    fun disconnect() {
        if (!connected)
            return
        socket.close()
        input.close()
        output.close()
        connected = false
        onDisconnected()
    }

    val serializable: SerializableDevice
        get() = SerializableDevice(id, name, isMobile)

    private fun onDisconnected() {
        devices.remove(this)
        if (listener != null)
            listener!!.onDisconnected()
    }


}

