package com.anto426.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity

/** Isolated empty host for SDK interaction tests; never ships in release builds. */
class LiquidTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }
}
