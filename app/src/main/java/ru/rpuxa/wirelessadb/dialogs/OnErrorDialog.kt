package ru.rpuxa.wirelessadb.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.on_error_dialog.view.*
import ru.rpuxa.wirelessadb.R


class OnErrorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val code = arguments?.getInt("errorCode")
        val builder = AlertDialog.Builder(context)
        val dialogView = activity.layoutInflater.inflate(R.layout.on_error_dialog, null)

        dialogView.cancel_dialog_btn.setOnClickListener {
            this.dialog.cancel()
        }

        dialogView.do_btn.setOnClickListener {
            if (code == 10061) {
                /*if (CoreServer.fixAdb10061())
                    this.dialog.cancel()
                else
                    dialogView.error_msg.text = getString(R.string.usb_error_msg)*/
                //откуда бы взять здесь device
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.stackoverflow.com")))
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