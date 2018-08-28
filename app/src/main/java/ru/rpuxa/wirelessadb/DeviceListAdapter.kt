package ru.rpuxa.wirelessadb

import android.app.Activity
import android.os.Handler
import android.os.Message
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
import ru.rpuxa.core.SerializableDevice

class DeviceListAdapter(
        private val inflater: LayoutInflater,
        private var devices: Array<SerializableDevice>
) : BaseAdapter() {

    init {
        devices = arrayOf(
                SerializableDevice(1, "abvgd", false),
                SerializableDevice(1, "abvgd2", true)
        )


    }

    var connectedDevicePosition: Int? = null

    override fun getCount() = devices.size

    override fun getItem(position: Int) = devices[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = convertView ?: inflater.inflate(R.layout.list_item, parent, false)!!
        val activity = itemView.context as Activity

        val device = devices[position]

        itemView.device_icon.setImageResource(
                if (device.isMobile) R.drawable.phone else R.drawable.pc
        )
        itemView.device_name.text = device.name

        /*if (device.isMobile)
            itemView.connect_btn.visibility = View.GONE*/

        itemView.connect_btn.setOnClickListener {

            itemView.connect_btn.visibility = View.INVISIBLE
            itemView.progress_bar_connect.visibility = View.VISIBLE

            val handler =
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    if (true) {
                        connectedDevicePosition = position
                        itemView.progress_bar_connect.visibility = View.INVISIBLE
                        itemView.connect_indicator.visibility = View.VISIBLE

                        activity.include.connected_device_name.text = device.name
                        activity.include.connected_device_icon.setImageResource(
                                if (device.isMobile) R.drawable.phone else R.drawable.pc
                        )

                        animateConnected(activity, false)
                    } else {
                        Toast.makeText(activity, "Connect failed", Toast.LENGTH_LONG).show()
                    }
                }
            }

            Thread {
                //CoreServer.connectAdb(device)
                emulateConnect()
                handler.sendEmptyMessage(1)
            }.start()
        }
        activity.include.disconnect_btn.setOnClickListener {
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

    private fun emulateConnect() {
        Thread.sleep(3000)
    }
}