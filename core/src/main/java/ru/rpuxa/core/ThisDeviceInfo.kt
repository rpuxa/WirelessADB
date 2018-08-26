package ru.rpuxa.core

import org.jetbrains.annotations.NotNull
import java.io.*
import java.util.*

private const val UNDEFINED = "\$\$\$\$\$\$\$\$\$<UNDEFINED>\$\$\$\$\$\$\$\$\$\$"

abstract class ThisDeviceInfo {
    abstract val baseName: String
    abstract val filesDir: File
    abstract val isMobile: Boolean


    open var name: String = UNDEFINED
        get() = try {
            if (field == UNDEFINED) {
                ObjectInputStream(FileInputStream(File(filesDir, "name"))).use {
                    it.readObject() as String
                }
            } else
                field
        } catch (e: Exception) {
            ObjectOutputStream(FileOutputStream(File(filesDir, "name"))).use {
                it.writeObject(baseName)
            }
            baseName
        }
        set(value) {
            ObjectOutputStream(FileOutputStream(File(filesDir, "name"))).use {
                it.writeObject(value)
            }
            field = value
        }


    @NotNull
    open val id: Long? = null
        get() = try {
            field ?: ObjectInputStream(FileInputStream(File(filesDir, "id"))).use {
                it.readObject() as Long
            }
        } catch (e: Exception) {
            val id = Random().nextLong()
            ObjectOutputStream(FileOutputStream(File(filesDir, "id"))).use {
                it.writeObject(id)
            }
            id
        }
}
