package ru.rpuxa.wirelessadb

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
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

        itemView.disconnect_btn.setOnClickListener {
            //CoreServer.connectAdb(device)
            if (true) {
                // itemView.visibility = View.GONE
                animateConnected(activity, false)

            } else {
                Toast.makeText(activity, "Connect failed", Toast.LENGTH_LONG).show()
            }
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


}