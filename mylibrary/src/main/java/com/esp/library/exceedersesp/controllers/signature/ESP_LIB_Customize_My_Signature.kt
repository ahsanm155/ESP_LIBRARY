package com.esp.library.exceedersesp.controllers.signature

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO
import kotlinx.android.synthetic.main.esp_lib_gradientcurvetoolbar.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.ibToolbarBack

class ESP_LIB_Customize_My_Signature : ESP_LIB_BaseActivity() {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ESP_LIB_ESPApplication.getInstance().applicationTheme)
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_customize_my_signature)
        initailize()
        selectStyleTab()

        val mSwitchMultiButton = findViewById<View>(R.id.switchmultibutton) as ESP_LIB_SwitchMultiButton
        mSwitchMultiButton.setOnSwitchListener { position, tabText ->

            if (position == 0)
                selectStyleTab()
            else if (position == 1)
                selectDrawTab()
            else if (position == 2)
                selectUploadTab()

        }


    }

    private fun initailize() {
        fragmentManager = supportFragmentManager
        setSupportActionBar(gradientcurvetoolbar)
        ibToolbarBack.setOnClickListener { onBackPressed() }
        val mySignatureText = getString(R.string.esp_lib_text_customize_mysignature)
        headingtext.text = mySignatureText

    }


    private fun selectStyleTab() {
        val args = Bundle()
        var espLibSignaturedao: ESP_LIB_SignatureDAO? = null
        if (intent.getSerializableExtra("body") != null) {
            espLibSignaturedao = intent.getSerializableExtra("body") as ESP_LIB_SignatureDAO
            args.putSerializable("body", intent.getSerializableExtra("body") as ESP_LIB_SignatureDAO)
        }
        val submit_request = ESP_LIB_SelectStyleFragment.newInstance(espLibSignaturedao)
        submit_request.arguments = args
        val ft = fragmentManager?.beginTransaction()
        ft?.replace(R.id.request_fragment, submit_request)
        ft?.commit()
    }

    private fun selectDrawTab() {

        val submit_request = ESP_LIB_DrawFragment.newInstance()
        val ft = fragmentManager?.beginTransaction()
        ft?.replace(R.id.request_fragment, submit_request)
        ft?.commit()
    }

    private fun selectUploadTab() {

        val submit_request = ESP_LIB_UploadFragment.newInstance()
        val ft = fragmentManager?.beginTransaction()
        ft?.replace(R.id.request_fragment, submit_request)
        ft?.commit()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)

                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}