package com.example.locklistener.lock_services.config;



import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class RunMode {

    public static final int POWER_SAVING = 0;

    public static final int HIGH_POWER_CONSUMPTION = 1;


    @IntDef(flag = true, value = {POWER_SAVING, HIGH_POWER_CONSUMPTION})
    @Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Model {
    }

    private static @Model
    int value = POWER_SAVING;

    public static void setShape(@Model int values) {
        value = values;
    }

    @Model
    public static int getShape() {
        return value;
    }
}