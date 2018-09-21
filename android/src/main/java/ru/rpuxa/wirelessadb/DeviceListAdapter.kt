package ru.rpuxa.wirelessadb

import android.app.Activity
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
import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.trd
import ru.rpuxa.wirelessadb.settings.AndroidSettings

class DeviceListAdapter(private val inflater: LayoutInflater, private val listView: ViewGroup) : BaseAdapter() {

    private var devices = ArrayList<Device>()
    private var deviceViews = ArrayList<DeviceView>()
    private val activity: FragmentActivity
        get() = inflater.context as FragmentActivity

    /**
     * Метод для добавления нового девайса в адаптер
     */
    fun addDevice(device: Device) {
        activity.runOnUiThread {
            devices.add(device)
            deviceViews.add(DeviceView(device.getView(), device))
            notifyDataSetChanged()
        }
    }

    /**
     * Аналогичное удаление
     */
    fun removeDevice(device: Device) {
        activity.runOnUiThread {
            for (i in devices.indices.reversed())
                if (devices[i].id == device.id) {
                    devices.removeAt(i)
                    deviceViews.removeAt(i)
                    break
                }

            trd {
                if (InternalServerController.checkAdb(device))
                    activity.runOnUiThread {
                        onDisconnected()
                    }
            }
            notifyDataSetChanged()
        }
    }

    fun onAdbDisconnected(device: Device) {
        activity.runOnUiThread {
            onDisconnected()
        }
    }

    fun onAdbConnected(device: Device) {
        activity.runOnUiThread {
            val view = deviceViews.find { it.device.id == it.device.id }!!.view
            view.progress_bar_connect.visibility = View.INVISIBLE
            view.connect_indicator.visibility = View.VISIBLE

            activity.include.connected_device_name.text = device.name
            activity.include.connected_device_icon.setImageResource(
                    if (device.isMobile) R.drawable.phone else R.drawable.pc
            )
            animateConnected(activity, false)
            onConnected()
        }
    }

    private fun onDisconnected() {
        for (item in deviceViews) {
            item.view.connect_indicator.visibility = View.INVISIBLE
            item.view.progress_bar_connect.visibility = View.INVISIBLE
            item.view.connect_btn.visibility = View.VISIBLE
        }
        animateConnected(activity, true)
    }

    private fun onConnecting() {
        for (item in deviceViews)
            item.view.connect_btn.isEnabled = false
    }

    private fun onConnected() {
        for (item in deviceViews) {
            item.view.connect_btn.isEnabled = true
            item.view.connect_btn.visibility = View.INVISIBLE
        }
    }

    private fun Device.getView(): View {
        val itemView = inflater.inflate(R.layout.list_item, listView, false)
        val activity = itemView.context as FragmentActivity

        itemView.device_icon.setImageResource(
                if (isMobile) R.drawable.phone else R.drawable.pc
        )
        itemView.device_name.text = name

        itemView.connect_btn.setOnClickListener {
            connectAdb(itemView)
        }

        activity.include.disconnect_btn.setOnClickListener {
            InternalServerController.disconnectAdb(this)
        }

        val switch = activity.auto_connect_switch
        val autoConnect = AndroidSettings.isAutoConnect(this)
        if (autoConnect) {
            connectAdb(itemView)
        }
        switch.isChecked = autoConnect
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AndroidSettings.autoConnectIds.add(id)
            } else
                AndroidSettings.autoConnectIds.remove(id)
        }

        return itemView
    }

    private fun Device.connectAdb(itemView: View) {
        onConnecting()
        itemView.connect_btn.visibility = View.INVISIBLE
        itemView.progress_bar_connect.visibility = View.VISIBLE
        trd { InternalServerController.connectAdb(this) }
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = deviceViews[position].view

    class DeviceView(val view: View, val device: Device)
}