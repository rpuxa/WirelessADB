package ru.rpuxa.core.internalServer

import java.io.Serializable

class DeviceInfoSerializable(override val filesDir: String,
                             override val isMobile: Boolean,
                             override var adbPath: String,
                             override var name: String) : DeviceInfo(), Serializable {

    constructor(info: DeviceInfo) : this(info.filesDir, info.isMobile, info.adbPath, info.name)
}