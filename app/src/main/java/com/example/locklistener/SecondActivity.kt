package com.example.locklistener

import android.os.Bundle
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mai2)
        checkScreenOn()
    }

    private fun checkScreenOn() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isScreenOn) {
            finishAffinity();
        }
    }
}