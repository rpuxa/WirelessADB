package ru.rpuxa.wirelessadb

import android.app.Activity
import android.widget.Toast

fun Activity.toast(msg: String, isLong: Boolean = true) =
        runOnUiThread {
            Toast.makeText(this, msg, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
                    .show()
        }