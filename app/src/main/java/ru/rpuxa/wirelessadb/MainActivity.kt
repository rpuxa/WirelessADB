package ru.rpuxa.wirelessadb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.listeners.ServerListener
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.core.trd
import ru.rpuxa.wirelessadb.dialogs.DeviceRenameDialog
import ru.rpuxa.wirelessadb.dialogs.LanguageDialog
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
            if (!ANDROID_DEVICE_INFO.isWifiEnable) {
                toast(getString(R.string.not_enabled_wifi))
                power_switch.isChecked = false
            } else
                trd {
                    onConnectChange(CoreServer.isAvailable)
                }
        }

        trd {
            if (CoreServer.isAvailable)
                onConnectChange(false)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                R.id.menu_item_language -> {
                    LanguageDialog().show(supportFragmentManager, "Language")
                    true
                }
                R.id.menu_item_rename -> {
                    DeviceRenameDialog().show(supportFragmentManager, "Rename")
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun onConnectChange(disconnect: Boolean) {
        runOnUiThread {
            power_switch.isChecked = !disconnect

            val visibility = if (disconnect) View.INVISIBLE else View.VISIBLE
            allViews.forEach { it.visibility = visibility }
            searchingDevices = !disconnect
            if (!disconnect) {
                status_bar_text.text = getString(R.string.searching_devices)
                include.visibility = View.INVISIBLE
                startSearchingDevices()
            } else {
                status_bar_text.text = getString(R.string.search_disabled)
                trd { CoreServer.closeServer() }
            }
        }
    }

    override fun onPause() {
        SettingsCache.save(AndroidSettings, ANDROID_DEVICE_INFO)
        super.onPause()
    }

    private var searchingDevices = true
    private fun startSearchingDevices() {
        CoreServer.startServer(ANDROID_DEVICE_INFO, object : ServerListener {
            override fun onAdd(device: Device) {
                adapter.addDevice(device)
            }

            override fun onRemove(device: Device) {
                adapter.removeDevice(device)
            }
        })
        trd {
            Thread.sleep(1000)
            while (searchingDevices) {
                if (CoreServer.isAvailable) {
                    Thread.sleep(1000)
                } else
                    onConnectChange(true)
            }
        }
    }
}