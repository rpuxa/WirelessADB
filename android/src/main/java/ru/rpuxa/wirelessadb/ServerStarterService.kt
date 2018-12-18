package ru.rpuxa.wirelessadb

import android.app.Service
import android.content.Intent
import ru.rpuxa.internalServer.InternalServer
import kotlin.concurrent.thread

class ServerStarterService : Service() {

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // startForeground(startId, Notification())
        thread {
            InternalServer.init()
        }
        return Service.START_STICKY
    }
}
