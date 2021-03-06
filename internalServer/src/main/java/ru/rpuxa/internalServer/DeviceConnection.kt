package ru.rpuxa.internalServer

import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.Message
import ru.rpuxa.internalServer.InternalServer.devices
import ru.rpuxa.internalServer.InternalServer.info
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

internal class DeviceConnection(
        private val socket: Socket,
        private val output: ObjectOutputStream,
        private val input: ObjectInputStream,
        internal val ip: InetAddress
) {

    internal var id = -1L
    private var name = ""
    private var isMobile = false

    private var listener: DeviceListener? = null
    private var connected = AtomicBoolean(false)

    init {
        try {
            sendMessage(ID, info.id!!)
            sendMessage(NAME, info.name)
            sendMessage(TYPE, info.isMobile)

            thread {
                Thread.sleep(2000)
                if (!connected.get()) {
                    output.close()
                    input.close()
                }
            }
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
                this.connected.set(true)
                thread {
                    while (connected.get() && sendMessage(CHECK))
                        Thread.sleep(1000)
                    disconnect()
                }
                startListening()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            onDisconnected()
        }
    }

    private fun readMessage() = input.readObject() as Message

    @Synchronized
    internal fun sendMessage(command: Int, data: Any? = null) = try {
        output.writeObject(Message(command, data))
        output.flush()
        true
    } catch (e: IOException) {
        onDisconnected()
        false
    }

    private fun startListening() {
        try {
            while (connected.get()) {
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
            CONNECT_ADB ->
                if (isMobile && !info.isMobile) {
                    val res = changeADB(ip)
                    if (res == 0)
                        sendMessage(ADB_OK)
                    else
                        sendMessage(ADB_ERROR, res)
                } else
                    sendMessage(ADB_FAIL)


            ADB_CHECK ->
                if (isMobile && !info.isMobile && checkADB(ip)) {
                    sendMessage(ADB_OK)
                } else
                    sendMessage(ADB_FAIL)


            DISCONNECT_ADB ->
                if (isMobile && !info.isMobile && changeADB(ip, false) == 0) {
                    sendMessage(ADB_OK)
                } else
                    sendMessage(ADB_FAIL)


            FIX_ADB_10061 ->
                if (isMobile && !info.isMobile && fixAdb10061(ip)) {
                    sendMessage(ADB_OK)
                } else
                    sendMessage(ADB_FAIL)
        }
    }

    private fun disconnect() {
        if (!connected.get())
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
        onDisconnected()
    }

    internal val serializable: Device
        get() = Device(id, name, isMobile)

    private fun onDisconnected() {
        connected.set(false)
        devices.remove(this)
        if (listener != null)
            listener!!.onDisconnected()
    }

    internal fun sendMessageAndWaitResponse(command: Int, data: Any? = null): Message? {
        var answer: Message? = null
        var disconnect = false
        listener = object : DeviceListener {
            override fun onGetMessage(message: Message) {
                if (message.command != CHECK)
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

