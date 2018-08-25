package ru.rpuxa.wirelessadb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.list_item.view.*
import ru.rpuxa.wirelessadb.core.CoreServer
import ru.rpuxa.wirelessadb.core.SerializableDevice

class DeviceListAdapter(
        private val inflater: LayoutInflater,
        private val devices: Array<SerializableDevice>
) : BaseAdapter() {

    override fun getCount() = devices.size

    override fun getItem(position: Int) = devices[position]


    override fun getItemId(position: Int) = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = convertView ?: inflater.inflate(R.layout.list_item, parent, false)!!

        val device = devices[position]
        if (device.isMobile) itemView.device_icon.setImageResource(R.drawable.phone)
        itemView.device_name.text = device.name

        if (device.isMobile)
            itemView.connect_btn.visibility = View.GONE

        itemView.connect_btn.setOnClickListener {
            if (CoreServer.connectAdb(device)) {
                //получилось подсоединить
            } else {
                //не получилось (тут можно вывести сообщение)
            }
        }

        return itemView
    }

}