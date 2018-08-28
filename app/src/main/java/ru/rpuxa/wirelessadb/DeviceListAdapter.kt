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
import ru.rpuxa.core.Device

class DeviceListAdapter(
        private val inflater: LayoutInflater,
        private var devices: Array<Device>
) : BaseAdapter() {

    private var devicesItemView: ArrayList<View> = arrayListOf()

    init {
        devices = arrayOf(
                Device(1, "abvgd", false),
                Device(1, "abvgd2", true)
        )

        for (device in devices) {
            devicesItemView.add(getDeviceView(device))
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

    private fun getDeviceView(device: Device): View {
        //Еще не разобрался как родителя передать
        val itemView = inflater.inflate(R.layout.list_item, null, false)
        val activity = itemView.context as Activity

        itemView.device_icon.setImageResource(
                if (device.isMobile) R.drawable.phone else R.drawable.pc
        )
        itemView.device_name.text = device.name

        itemView.connect_btn.setOnClickListener {
            onConnecting()
            itemView.connect_btn.visibility = View.INVISIBLE
            itemView.progress_bar_connect.visibility = View.VISIBLE

            val handler = Handler()
            Thread {
                emulateConnect()
                handler.post {
                    if (true) {
                        itemView.progress_bar_connect.visibility = View.INVISIBLE
                        itemView.connect_indicator.visibility = View.VISIBLE

                        activity.include.connected_device_name.text = device.name
                        activity.include.connected_device_icon.setImageResource(
                                if (device.isMobile) R.drawable.phone else R.drawable.pc
                        )
                        animateConnected(activity, false)
                        onConnected()
                    } else {
                        Toast.makeText(activity, "Connect failed", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        activity.include.disconnect_btn.setOnClickListener {
            toDisconnectViewMode()
            animateConnected(activity, true)
        }

        return itemView
    }

    override fun getCount() = devices.size

    override fun getItem(position: Int) = devices[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View = devicesItemView[position]

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