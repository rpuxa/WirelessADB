package ru.rpuxa.wirelessadb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ru.rpuxa.wirelessadb.core.CoreServer
import ru.rpuxa.wirelessadb.core.SerializableDevice
import ru.rpuxa.wirelessadb.core.ThisDeviceInfo

class MainActivity : AppCompatActivity() {

    private lateinit var allViews: Array<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        allViews = arrayOf(
                device_list_view
        )

        power_switch.setOnClickListener { _ ->
            Thread {
                onConnectChange(CoreServer.isAvailable)
            }.start()
        }

        onConnectChange(true)
    }


    private fun onConnectChange(disconnected: Boolean) {
        runOnUiThread {
            power_switch.isChecked = !disconnected

            val visibility = if (disconnected) View.INVISIBLE else View.VISIBLE
            allViews.forEach { it.visibility = visibility }
            searchingDevices = !disconnected
            if (!disconnected) {
                status_bar_text.text = getString(R.string.searching_devices)
                startSearchingDevices()
            } else {
                status_bar_text.text = getString(R.string.service_switched_off)
                CoreServer.closeServer()
            }
        }
    }

    private var searchingDevices = true
    private fun startSearchingDevices() {
        Thread {
            CoreServer.startServer(AndroidDeviceInfo())
            Thread.sleep(1000)
            var lastDeviceArray: Array<SerializableDevice>? = null
            while (searchingDevices) {
                val deviceArray = CoreServer.getDevicesList()
                if (deviceArray != null) {
                    if (lastDeviceArray == null || !lastDeviceArray.equalsElements(deviceArray)) {
                        val deviceListAdapter = DeviceListAdapter(layoutInflater, deviceArray)
                        runOnUiThread {
                            device_list_view.adapter = deviceListAdapter
                        }
                    }
                    lastDeviceArray = deviceArray
                } else
                    onConnectChange(true)
                Thread.sleep(1000)
            }
            runOnUiThread {
                device_list_view.adapter = null
            }
        }.start()
    }

    private fun Array<SerializableDevice>.equalsElements(arr: Array<SerializableDevice>): Boolean {
        for (element in this) {
            if (arr.find { it.id == element.id } == null) {
                return false
            }
        }

        return true
    }

    inner class AndroidDeviceInfo : ThisDeviceInfo() {
        override val baseName = "Android Phone"
        override val filesDir = this@MainActivity.filesDir!!
        override val isMobile = true
    }
}
