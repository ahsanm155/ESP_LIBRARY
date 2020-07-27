package com.esp.library.utilities.common;

import android.util.Log;

public class ESP_LIB_CustomLogs {

    static String TAG="ESP";

    public static void displayLogs(String msg)
    {
        Log.i(TAG,msg+"");
    }
}
