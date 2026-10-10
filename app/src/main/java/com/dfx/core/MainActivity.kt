package com.dfx.core

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // anti-debug / emulator light checks
        if (AntiDetect.isDebuggerAttached() || AntiDetect.isEmulator()) {
            // silent exit style – change behaviour if needed
            finishAffinity()
            return
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 120, 48, 48)
        }

        val btn = Button(this).apply {
            text = "Enable Overlay"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    !Settings.canDrawOverlays(this@MainActivity)
                ) {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "Grant overlay then press again",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    startService(Intent(this@MainActivity, FloatingService::class.java))
                    moveTaskToBack(true)
                }
            }
        }
        root.addView(btn)
        setContentView(root)
    }
}
