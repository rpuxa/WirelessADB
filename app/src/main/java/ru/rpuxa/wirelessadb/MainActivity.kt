package ru.rpuxa.wirelessadb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import ru.rpuxa.wirelessadb.android.DeviceListAdapter
import ru.rpuxa.wirelessadb.core.CoreServer
import ru.rpuxa.wirelessadb.core.ThisDeviceInfo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Для всех действий класс CoreServer

        val deviceArray = CoreServer.getDevicesList()
        if (deviceArray != null) {
            val deviceListAdapter = DeviceListAdapter(this, deviceArray)
            deviceListView.adapter = deviceListAdapter
        } else
            statusBarText.text = getString(R.string.devices_not_found)
    }

    inner class AndroidDeviceInfo : ThisDeviceInfo() {
        override val baseName = "Android Phone"
        override val filesDir = this@MainActivity.filesDir!!
        override val isMobile = true
    }
}
