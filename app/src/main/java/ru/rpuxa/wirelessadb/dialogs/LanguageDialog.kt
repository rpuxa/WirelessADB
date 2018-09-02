package ru.rpuxa.wirelessadb.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import ru.rpuxa.wirelessadb.settings.AndroidSettings
import ru.rpuxa.wirelessadb.settings.Languages
import java.util.*

class LanguageDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick a language")
                .setItems(arrayOf(Languages.ENGLISH.toString(), Languages.RUSSIAN.toString())) { _, _ ->
                    val res = context.resources
                    val dm = res.displayMetrics
                    val conf = res.configuration
                    //Старый способ смены язка, новый пока не нашел
                    conf.locale = Locale(Languages.ENGLISH.toString().toLowerCase())
                    res.updateConfiguration(conf, dm)
                    AndroidSettings.language = Languages.ENGLISH
                }

        return builder.create()
    }
}