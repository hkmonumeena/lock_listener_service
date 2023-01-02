package com.example.locklistener.lock_services;

import android.util.Log;

public class KeepAliveRuning implements IKeepAliveRuning {
    @Override
    public void onRuning() {
        Log.e("runing?KeepAliveRuning", "true");
    }

    @Override
    public void onStop() {
        Log.e("runing?KeepAliveRuning", "false");
    }
}
