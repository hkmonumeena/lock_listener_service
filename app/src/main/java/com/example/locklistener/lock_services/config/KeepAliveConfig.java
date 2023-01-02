package com.example.locklistener.lock_services.config;


import com.example.locklistener.R;


public class KeepAliveConfig {


    public static final int JOB_TIME = 10000;
    public static final int FOREGROUD_NOTIFICATION_ID = 8888;

    public static final String RUN_MODE = "RUN_MODE";

    public static final String PROCESS_ALIVE_ACTION = "PROCESS_ALIVE_ACTION";
    public static final String PROCESS_STOP_ACTION = "PROCESS_STOP_ACTION";
    public static ForegroundNotification foregroundNotification = null;

    public static int runMode = RunMode.getShape();

    public static String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";
    public static String TITLE = "TITLE";
    public static String CONTENT = "CONTENT";
    public static String RES_ICON = "RES_ICON";
    public static int DEF_ICONS = R.mipmap.ic_launcher;
    public static String SP_NAME = "KeepAliveConfig";

}
