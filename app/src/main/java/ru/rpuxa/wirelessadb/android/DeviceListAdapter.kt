package ru.rpuxa.wirelessadb.android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.list_item.view.*
import ru.rpuxa.wirelessadb.R
import ru.rpuxa.wirelessadb.core.SerializableDevice

class DeviceListAdapter(context: Context, private val devices: Array<SerializableDevice>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    override fun getCount(): Int {
        return devices.size
    }

    override fun getItem(position: Int): Any {
        return devices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView = convertView
        if (itemView == null) itemView = inflater.inflate(R.layout.list_item, parent, false)

        val device: SerializableDevice = devices[position]
        if (device.isMobile) itemView!!.deviceIcon.setImageResource(R.drawable.phone)
        itemView!!.deviceName.text = device.name

        return itemView
    }

    //Надо повесить слушатели на connectBtn
}