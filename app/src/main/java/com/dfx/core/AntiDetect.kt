package com.dfx.core

import android.os.Build
import android.os.Debug
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object AntiDetect {

    fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    fun isEmulator(): Boolean {
        val indicators = listOf(
            Build.FINGERPRINT.startsWith("generic"),
            Build.FINGERPRINT.contains("emulator"),
            Build.MODEL.contains("Emulator"),
            Build.MODEL.contains("Android SDK"),
            Build.MANUFACTURER.contains("Genymotion"),
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"),
            Build.PRODUCT.contains("sdk"),
            Build.PRODUCT.contains("emulator"),
            Build.HARDWARE.contains("goldfish"),
            Build.HARDWARE.contains("ranchu")
        )
        return indicators.any { it }
    }

    fun isRooted(): Boolean {
        val paths = arrayOf(
            "/system/bin/su", "/system/xbin/su", "/sbin/su",
            "/data/local/xbin/su", "/data/local/bin/su",
            "/system/sd/xbin/su", "/system/bin/failsafe/su",
            "/data/local/su", "/su/bin/su"
        )
        if (paths.any { File(it).exists() }) return true
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("which", "su"))
            BufferedReader(InputStreamReader(p.inputStream)).readLine() != null
        } catch (_: Exception) {
            false
        }
    }

    // simple string decode to avoid plain strings in binary
    fun d(encoded: String, key: Int = 0x5A): String {
        return encoded.map { (it.code xor key).toChar() }.joinToString("")
    }
}
