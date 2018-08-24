package ru.rpuxa.wirelessadb

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.rpuxa.wirelessadb.core.ThisDeviceInfo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Для всех действий класс CoreServer
    }


    inner class AndroidDeviceInfo : ThisDeviceInfo() {
        override val baseName = "Android Phone"
        override val filesDir = this@MainActivity.filesDir!!
        override val isMobile = true
    }
}
