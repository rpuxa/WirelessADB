package ru.rpuxa.wirelessadb.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.device_rename_dialog.view.*
import ru.rpuxa.core.checkName
import ru.rpuxa.wirelessadb.R
import ru.rpuxa.wirelessadb.settings.AndroidSettings
import ru.rpuxa.wirelessadb.toast

class DeviceRenameDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)
        val dialogView = activity.layoutInflater.inflate(R.layout.device_rename_dialog, null)
        dialogView.name_text.setText(AndroidSettings.deviceName)

        dialogView.cancel_dialog_btn.setOnClickListener {
            dialog.cancel()
        }

        dialogView.rename_dialog_btn.setOnClickListener {
            val name = dialogView.name_text.text.toString()
            if (checkName(name)) {
                AndroidSettings.deviceName = name
                dialog.cancel()
            } else {
                activity.toast("Only numbers, the Roman and Cyrillic alphabet, \"-\" and \"_\"," +
                        " from 4 to 16 characters")
            }
        }

        builder.setView(dialogView)

        return builder.create()
    }
}
