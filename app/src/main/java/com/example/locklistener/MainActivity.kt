package com.example.locklistener

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.locklistener.lock_services.DevicesLaunchConfig
import com.example.locklistener.lock_services.KeepAliveManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  KeepAliveManager.launcherSyskeepAlive(this)  // for redirect settings

    }
}