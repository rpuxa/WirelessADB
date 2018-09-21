package ru.rpuxa.wirelessadb.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.on_error_dialog.view.*
import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.trd
import ru.rpuxa.wirelessadb.R


const val ERROR_CODE = "errorCode"
const val DEVICE = "device"

class OnErrorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val code = arguments!!.getInt(ERROR_CODE)
        val device = arguments!!.getSerializable(DEVICE) as Device
        val builder = AlertDialog.Builder(context)
        val dialogView = activity.layoutInflater.inflate(R.layout.on_error_dialog, null)

        dialogView.cancel_dialog_btn.setOnClickListener {
            this.dialog.cancel()
        }

        dialogView.do_btn.setOnClickListener {
            if (code == 10061) {
                trd {
                    val fixed = InternalServerController.fixAdb10061(device)
                    (context as Activity).runOnUiThread {
                        if (fixed)
                            this.dialog.cancel()
                        else
                            dialogView.error_msg.text = getString(R.string.usb_error_msg)
                    }
                }
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://stackoverflow.com/search?q=adb+error+$code")))
            }
        }

        dialogView.error_code.text = code.toString()

        if (code == 10061) {
            dialogView.do_btn.text = getString(R.string.fix_btn)
            dialogView.error_msg.text = getString(R.string.known_error_msg)
        } else {
            dialogView.do_btn.text = getString(R.string.search_btn)
            dialogView.error_msg.text = getString(R.string.default_error_msg)
        }

        builder.setView(dialogView)

        return builder.create()
    }
}