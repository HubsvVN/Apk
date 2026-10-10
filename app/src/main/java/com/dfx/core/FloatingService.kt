package com.dfx.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class FloatingService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var menu: View
    private var esp = false
    private var line = false
    private var aim = false
    private var norecoil = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundSilent()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        menu = buildMenu()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= 26)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 180
        }

        menu.setOnTouchListener(object : View.OnTouchListener {
            var ix = 0; var iy = 0; var px = 0; var py = 0
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        ix = params.x; iy = params.y
                        px = e.rawX.toInt(); py = e.rawY.toInt()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = ix + (e.rawX.toInt() - px)
                        params.y = iy + (e.rawY.toInt() - py)
                        wm.updateViewLayout(menu, params)
                        return true
                    }
                }
                return false
            }
        })
        wm.addView(menu, params)
    }

    private fun startForegroundSilent() {
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel("dfx", "svc", NotificationManager.IMPORTANCE_MIN)
            ch.setShowBadge(false)
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(ch)
            val n = Notification.Builder(this, "dfx")
                .setContentTitle("")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build()
            startForeground(1, n)
        }
    }

    private fun buildMenu(): View {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#E6121220"))
            cornerRadius = 18f
            setStroke(2, Color.parseColor("#334CAF50"))
        }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 22, 28, 22)
            background = bg
            elevation = 16f
        }

        val title = TextView(this).apply {
            text = "Δ  CORE"
            setTextColor(Color.WHITE)
            textSize = 15f
            setPadding(0, 0, 0, 14)
        }
        root.addView(title)

        fun sw(label: String, get: () -> Boolean, set: (Boolean) -> Unit): Switch {
            return Switch(this).apply {
                text = label
                setTextColor(Color.parseColor("#CCFFFFFF"))
                isChecked = get()
                setOnCheckedChangeListener { _, v ->
                    set(v)
                    NativeBridge.setFeature(label, v)
                }
            }
        }

        root.addView(sw("ESP Box") { esp } { esp = it })
        root.addView(sw("ESP Line") { line } { line = it })
        root.addView(sw("Aim Assist") { aim } { aim = it })
        root.addView(sw("No Recoil") { norecoil } { norecoil = it })

        val close = Button(this).apply {
            text = "Hide"
            setOnClickListener { stopSelf() }
        }
        root.addView(close)
        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::menu.isInitialized) {
            try { wm.removeView(menu) } catch (_: Exception) {}
        }
    }
}
