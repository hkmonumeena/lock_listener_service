package com.example.locklistener

import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mai2)
        Log.e("jdukhni", "onCreate: 12")
        Toast.makeText(this, "activity open", Toast.LENGTH_SHORT).show()
        checkScreenOn()
    }

    private fun checkScreenOn() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isScreenOn) {
           // finishAffinity();
        }
    }
}