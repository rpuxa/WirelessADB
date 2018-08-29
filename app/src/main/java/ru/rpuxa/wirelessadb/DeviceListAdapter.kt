package ru.rpuxa.wirelessadb

import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.connected_device.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.listeners.AdbListener

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
        (inflater.context as Activity).runOnUiThread {
            for (i in devices.indices.reversed())
                if (devices[i].id == device.id) {
                    devices.removeAt(i)
                    devicesItemView.removeAt(i)
                    break
                }

            notifyDataSetChanged()
        }
    }

    private fun toDisconnectViewMode() {
        for (item in devicesItemView) {
            item.connect_indicator.visibility = View.INVISIBLE
            item.connect_btn.visibility = View.VISIBLE
        }
        animateConnected(devicesItemView[0].context as Activity, true)
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
        val activity = itemView.context as Activity

        itemView.device_icon.setImageResource(
                if (isMobile) R.drawable.phone else R.drawable.pc
        )
        itemView.device_name.text = name

        itemView.connect_btn.setOnClickListener {
            if (!CoreServer.checkAdb(this)) {
                onConnecting()
                itemView.connect_btn.visibility = View.INVISIBLE
                itemView.progress_bar_connect.visibility = View.VISIBLE

                val handler = Handler()
                Thread {
                    CoreServer.connectAdb(
                            this,
                            object : AdbListener {
                                override fun onConnect() {
                                    handler.post {
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
                                    Toast.makeText(activity, "Connect failed", Toast.LENGTH_LONG)
                                            .show()
                                }
                            }
                    )
                }.start()
            } else {
                Toast.makeText(activity, "You are connected to another device", Toast.LENGTH_LONG)
                        .show()
            }
        }

        activity.include.disconnect_btn.setOnClickListener {
            toDisconnectViewMode()
            animateConnected(activity, true)
        }

        return itemView
    }

    private fun animateConnected(activity: Activity, close: Boolean) {
        val animation = AnimationUtils.loadAnimation(activity,
                if (close)
                    R.anim.close_anim
                else
                    R.anim.open_anim
        )
        animation.duration = 500
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

    override fun getCount() = devices.size

    override fun getItem(position: Int) = devices[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) = devicesItemView[position]
}