package com.esp.library.exceedersesp.controllers

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.setup.ESP_LIB_LoginScreenActivity
import com.google.firebase.analytics.FirebaseAnalytics



class ESP_LIB_SplashScreenActivity : ESP_LIB_BaseActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.esp_lib_activity_splash_screen)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ESP_LIB_Shared.getInstance().callIntentWithTime(ESP_LIB_LoginScreenActivity::class.java, this, 2000, null)


    }

    companion object {

        var ACTIVITY_NAME = "controllers.SplashScreenActivity"
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }


}
