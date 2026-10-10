package com.dfx.core

object NativeBridge {
    init {
        try {
            System.loadLibrary("dfnative")
        } catch (_: Throwable) {
            // native optional – menu still works
        }
    }

    @JvmStatic
    external fun setFeature(name: String, enabled: Boolean)

    @JvmStatic
    external fun isReady(): Boolean
}
