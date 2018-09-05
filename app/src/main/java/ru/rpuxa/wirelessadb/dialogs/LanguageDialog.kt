package ru.rpuxa.wirelessadb.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import ru.rpuxa.wirelessadb.R
import ru.rpuxa.wirelessadb.settings.AndroidSettings
import ru.rpuxa.wirelessadb.settings.Languages

class LanguageDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.language_dialog_title)
                .setItems(Languages.values().map { it.languageName }.toTypedArray()) { _, id ->
                    AndroidSettings.setLanguage(Languages.values()[id], context)
                }

        return builder.create()
    }
}