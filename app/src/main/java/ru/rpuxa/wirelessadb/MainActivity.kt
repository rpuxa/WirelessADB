package ru.rpuxa.wirelessadb

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ru.rpuxa.wirelessadb.android.Debug
import ru.rpuxa.wirelessadb.core.ThisDeviceInfo
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Не убирай эту строчку это мне для дебага
        Debug.debug(AndroidDeviceInfo())
        //========================================
    }



    inner class AndroidDeviceInfo : ThisDeviceInfo() {
        override val baseName = "Android Phone"
        override val filesDir = this@MainActivity.filesDir!!
        override val isMobile = true
    }
}
