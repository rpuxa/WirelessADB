package ru.rpuxa.wirelessadb

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.connected_device.*
import kotlinx.android.synthetic.main.connected_device.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.listeners.AdbListener
import ru.rpuxa.core.trd
import ru.rpuxa.wirelessadb.dialogs.DEVICE
import ru.rpuxa.wirelessadb.dialogs.ERROR_CODE
import ru.rpuxa.wirelessadb.dialogs.OnErrorDialog
import ru.rpuxa.wirelessadb.settings.AndroidSettings
import java.util.concurrent.atomic.AtomicBoolean

class DeviceListAdapter(private val inflater: LayoutInflater, private val listView: ViewGroup) : BaseAdapter() {

    private var devices = ArrayList<Device>()
    private var devicesItemView = ArrayList<View>()

    /**
     * Метод для добавления нового девайса в адаптер
     */
    fun addDevice(device: Device) {
        (inflater.context as Activity).runOnUiThread {
            devices.add(device)
            devicesItemView.add(device.getView())
            notifyDataSetChanged()
        }
    }

    /**
     * Аналогичное удаление
     */
    fun removeDevice(device: Device) {
        val activity = inflater.context as Activity
        activity.runOnUiThread {
            for (i in devices.indices.reversed())
                if (devices[i].id == device.id) {
                    devices.removeAt(i)
                    devicesItemView.removeAt(i)
                    break
                }

            trd {
                if (CoreServer.checkAdb(device))
                    activity.runOnUiThread {
                        onDisconnected(activity)
                    }
            }
            notifyDataSetChanged()
        }
    }

    private fun onDisconnected(activity: Activity) {
        for (item in devicesItemView) {
            item.connect_indicator.visibility = View.INVISIBLE
            item.progress_bar_connect.visibility = View.INVISIBLE
            item.connect_btn.visibility = View.VISIBLE
        }
        animateConnected(activity, true)
    }

    private fun onConnecting() {
        for (item in devicesItemView)
            item.connect_btn.isEnabled = false
    }

    private fun onConnected() {
        for (item in devicesItemView) {
            item.connect_btn.isEnabled = true
            item.connect_btn.visibility = View.INVISIBLE
        }
    }

    private fun Device.getView(): View {
        val itemView = inflater.inflate(R.layout.list_item, listView, false)
        val activity = itemView.context as FragmentActivity
        val supportFragmentManager = activity.supportFragmentManager

        itemView.device_icon.setImageResource(
                if (isMobile) R.drawable.phone else R.drawable.pc
        )
        itemView.device_name.text = name

        val connectedAdb = AtomicBoolean(false)

        val adbListener = object : AdbListener {
            override fun onConnect() {
                connectedAdb.set(true)
                activity.runOnUiThread {
                    itemView.progress_bar_connect.visibility = View.INVISIBLE
                    itemView.connect_indicator.visibility = View.VISIBLE

                    activity.include.connected_device_name.text = name
                    activity.include.connected_device_icon.setImageResource(
                            if (isMobile) R.drawable.phone else R.drawable.pc
                    )
                    animateConnected(activity, false)
                    onConnected()
                }
            }

            override fun onDisconnect() {
                connectedAdb.set(false)
                activity.runOnUiThread {
                    onDisconnected(activity)
                }
            }

            override fun onError(code: Int) {
                val args = Bundle()
                val errorDialog = OnErrorDialog()
                args.putInt(ERROR_CODE, code)
                args.putSerializable(DEVICE, this@getView)
                errorDialog.arguments = args
                errorDialog.show(supportFragmentManager, "Error")
            }
        }

        itemView.connect_btn.setOnClickListener {
            connectAdb(itemView, adbListener)
        }

        activity.include.disconnect_btn.setOnClickListener {
            CoreServer.disconnectAdb(this, adbListener)
        }

        val switch = activity.auto_connect_switch
        val autoConnect = AndroidSettings.isAutoConnect(this)
        if (autoConnect) {
            connectAdb(itemView, adbListener)
        }
        switch.isChecked = autoConnect
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AndroidSettings.autoConnectIds.add(id)
            } else
                AndroidSettings.autoConnectIds.remove(id)
        }

        Thread {
            while (devices.find { it == this } != null) {
                val res = CoreServer.checkAdb(this)
                if (connectedAdb.get() != res) {
                    if (res)
                        adbListener.onConnect()
                    else
                        adbListener.onDisconnect()
                }

                Thread.sleep(1500)
            }
        }.start()

        return itemView
    }

    private fun Device.connectAdb(itemView: View, adbListener: AdbListener) {
        onConnecting()
        itemView.connect_btn.visibility = View.INVISIBLE
        itemView.progress_bar_connect.visibility = View.VISIBLE
        trd { CoreServer.connectAdb(this, adbListener) }
    }

    private fun animateConnected(activity: Activity, close: Boolean) {
        if (activity.include.visibility == if (close) View.VISIBLE else View.INVISIBLE) {
            val animation = AnimationUtils.loadAnimation(activity,
                    if (close)
                        R.anim.close_anim
                    else
                        R.anim.open_anim
            )
            activity.include.visibility = View.VISIBLE
            if (close) {
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        activity.include.visibility = View.INVISIBLE
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
            }
            activity.include.startAnimation(animation)
        }
    }

    override fun getCount() = devices.size

    override fun getItem(position: Int) = devices[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = devicesItemView[position]
}