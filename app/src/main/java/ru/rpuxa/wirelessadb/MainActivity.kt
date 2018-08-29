package ru.rpuxa.wirelessadb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.ThisDeviceInfo
import ru.rpuxa.core.listeners.ServerListener
import ru.rpuxa.wirelessadb.settings.AndroidSettings

class MainActivity : AppCompatActivity() {

    private lateinit var allViews: Array<View>
    private lateinit var adapter: DeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        allViews = arrayOf(
                device_list_view,
                include
        )
        adapter = DeviceListAdapter(layoutInflater, device_list_view)
        device_list_view.adapter = adapter

        power_switch.setOnClickListener {
            onConnectChange(CoreServer.isAvailable)
        }

        onConnectChange(!CoreServer.isAvailable)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            when (item?.itemId) {
                R.id.menu_item_language -> {
                    true
                }
                R.id.menu_item_rename -> true
                else -> super.onOptionsItemSelected(item)
            }

    private fun onConnectChange(disconnected: Boolean) {
        runOnUiThread {
            power_switch.isChecked = !disconnected

            val visibility = if (disconnected) View.INVISIBLE else View.VISIBLE
            allViews.forEach { it.visibility = visibility }
            searchingDevices = !disconnected
            if (!disconnected) {
                status_bar_text.text = getString(R.string.searching_devices)
                include.visibility = View.INVISIBLE
                startSearchingDevices()
            } else {
                status_bar_text.text = getString(R.string.service_switched_off)
                CoreServer.closeServer()
            }
        }
    }

    private var searchingDevices = true
    private fun startSearchingDevices() {
        CoreServer.startServer(AndroidDeviceInfo(), object : ServerListener {
            override fun onAdd(device: Device) {
                adapter.addDevice(device)
            }

            override fun onRemove(device: Device) {
                adapter.removeDevice(device)
            }
        })
        Thread {
            Thread.sleep(1000)
            while (searchingDevices) {
                if (CoreServer.isAvailable) {
                    Thread.sleep(1000)
                } else
                    onConnectChange(true)

            }
        }.start()
    }

    inner class AndroidDeviceInfo : ThisDeviceInfo() {
        override val settings = AndroidSettings
        override val filesDir = this@MainActivity.filesDir!!
        override val isMobile = true
    }
}
