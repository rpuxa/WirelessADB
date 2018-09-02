package ru.rpuxa.wirelessadb.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.device_rename_dialog.view.*
import ru.rpuxa.wirelessadb.R
import ru.rpuxa.wirelessadb.settings.AndroidSettings

class DeviceRenameDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        val dialogView = activity.layoutInflater.inflate(R.layout.device_rename_dialog, null)

        dialogView.cancel_dialog_btn.setOnClickListener {
            this.dialog.cancel()
        }

        dialogView.rename_dialog_btn.setOnClickListener {
            AndroidSettings.deviceName = dialogView.name_text.text.toString()
        }

        builder.setView(dialogView)

        return builder.create()
    }
}
