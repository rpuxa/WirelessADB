package ru.rpuxa.wirelessadb

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.core.trd
import ru.rpuxa.wirelessadb.dialogs.*
import ru.rpuxa.wirelessadb.settings.AndroidSettings

class MainActivity : AppCompatActivity() {

    companion object {
        private var listener: InternalServerController.InternalServerListener? = null
    }

    private lateinit var adapter: DeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        adapter = DeviceListAdapter(layoutInflater, device_list_view)
        device_list_view.adapter = adapter

        if (listener == null)
            listener = Listener()
        InternalServerController.setListener(listener!!)

        power_switch.setOnClickListener {
            onMainSwitchChange()
        }

        trd {
            if (InternalServerController.isAvailable) {
                onConnectChange(true)
                runOnUiThread {
                    power_switch.isChecked = true
                }
            }
        }

        if (AndroidSettings.autoStart) {
            onMainSwitchChange()
        }
    }

    private fun onMainSwitchChange() {
        if (!isWifiEnabled) {
            toast(getString(R.string.not_enabled_wifi))
            power_switch.isChecked = false
        } else
            trd {
                onConnectChange(!InternalServerController.isAvailable)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu!!.findItem(R.id.checkable_item).isChecked = AndroidSettings.autoStart
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item!!.itemId) {
                R.id.menu_item_language -> {
                    LanguageDialog().show(supportFragmentManager, "Language")
                    true
                }
                R.id.menu_item_rename -> {
                    DeviceRenameDialog().show(supportFragmentManager, "Rename")
                    true
                }
                R.id.checkable_item -> {
                    item.isChecked = !item.isChecked
                    AndroidSettings.autoStart = item.isChecked
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun onConnectChange(connect: Boolean) {
        runOnUiThread {
            if (connect) {
                status_bar_text.text = getString(R.string.searching_devices)
                include.visibility = View.INVISIBLE
                InternalServerController.startServer(WirelessAdb.deviceInfo, WirelessAdb.serverStarter)
            } else {
                status_bar_text.text = getString(R.string.search_disabled)
                InternalServerController.closeServer()
            }
        }
    }

    override fun onPause() {
        SettingsCache.save(AndroidSettings, WirelessAdb.deviceInfo)
        super.onPause()
    }

    private val isWifiEnabled: Boolean
        get() {
            val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return mWifi.isConnected
        }

    private inner class Listener : InternalServerController.InternalServerListener {
        override fun onServerConnected() {
            runOnUiThread {
                power_switch.isChecked = true
            }
            InternalServerController.setDeviceInfo(WirelessAdb.deviceInfo)
        }

        override fun onServerDisconnect() {
            runOnUiThread {
                power_switch.isChecked = false
            }
        }

        override fun serverStillWorking() {
            runOnUiThread {
                power_switch.isChecked = true
                device_list_view.visibility = View.VISIBLE
            }
        }

        override fun serverStillDisabled() {
            runOnUiThread {
                power_switch.isChecked = false
                device_list_view.visibility = View.INVISIBLE
            }
        }

        override fun onDeviceDetected(device: Device) {
            adapter.addDevice(device)
        }

        override fun onDeviceDisconnected(device: Device) {
            adapter.removeDevice(device)
            if (!isWifiEnabled) {
                toast(getString(R.string.not_enabled_wifi))
                InternalServerController.closeServer()
            }
        }

        override fun onAdbConnected(device: Device) {
            adapter.onAdbConnected(device)
        }

        override fun onAdbDisconnected(device: Device) {
            adapter.onAdbDisconnected()
        }

        override fun onAdbError(device: Device, code: Int) {
            runOnUiThread {
                adapter.onAdbError(device)
                val args = Bundle()
                val errorDialog = OnErrorDialog()
                args.putInt(ERROR_CODE, code)
                args.putSerializable(DEVICE, code)
                errorDialog.arguments = args
                errorDialog.show(supportFragmentManager, "Error")
            }
        }
    }
}