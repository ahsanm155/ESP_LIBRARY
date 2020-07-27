package com.esp.library.exceedersesp

import android.content.Intent
import android.os.Bundle
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.signature.ESP_LIB_My_Signature
import kotlinx.android.synthetic.main.esp_lib_activity_settings.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*

class ESP_LIB_Settings : ESP_LIB_BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_settings)
        initailize()

        rlclick.setOnClickListener{startActivity(Intent(this, ESP_LIB_My_Signature::class.java))}

    }

    private fun initailize() {
        setSupportActionBar(gradienttoolbar)
        ibToolbarBack.setOnClickListener { onBackPressed() }
        toolbarheading.text = getString(R.string.esp_lib_text_settings)
    }
}