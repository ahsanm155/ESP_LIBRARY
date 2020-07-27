package com.esp.library.exceedersesp.controllers

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.*
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import kotlinx.android.synthetic.main.esp_lib_activity_web_view.*
import kotlinx.android.synthetic.main.esp_lib_gradientcurvetoolbar.*

class ESP_LIB_WebViewScreenActivity : ESP_LIB_BaseActivity() {

    internal var TAG = javaClass.simpleName
    internal var context: ESP_LIB_BaseActivity? = null
    internal var heading: String? = null
    internal var url: String? = null
    internal var pDialog: AlertDialog? = null
    var pref: ESP_LIB_SharedPreference? = null
    var isIdenediServiceRunning = false


    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_web_view)
        initialize()
        // setGravity()
        val bnd = intent.extras
        if (bnd != null) {
            heading = bnd.getString("heading")
            headingtext.text = heading
             url = bnd.getString("url")
         //   url = "https://idenedi-prod-stag.azurewebsites.net/app_permission/?response_type=code&client_id=XSP1980031200&redirect_uri=https://qaesp.azurewebsites.net/login"

        }

        web_view.webViewClient = MyWebViewClient()
        web_view.webChromeClient = YourWebChromeClient();
        web_view.settings.javaScriptEnabled = true
        web_view.settings.domStorageEnabled = true
        // web_view.settings.userAgentString = "Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17"
        web_view.settings.setJavaScriptEnabled(true)
        //  web_view.addJavascriptInterface(JavaScriptInterface(), "AndroidInterface")
        web_view.loadUrl(url)


    }

    class YourWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {

            ESP_LIB_CustomLogs.displayLogs("WebViewScreenActivity alert message =  $message")

            result.confirm()
            return true
        }
    }

    private fun initialize() {
        context = this@ESP_LIB_WebViewScreenActivity
        setSupportActionBar(gradientcurvetoolbar)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        pref = ESP_LIB_SharedPreference(context)
        ibToolbarBack.setOnClickListener { finish() }
    }

    internal inner class JavaScriptInterface {
        @JavascriptInterface
        fun processHTML(formData: String) {
            ESP_LIB_CustomLogs.displayLogs("AWESOME_TAG form data: $formData")
        }
    }


    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)

            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            start_loading_animation()
        }

        override fun onPageFinished(view: WebView, str: String) {
            stop_loading_animation()

            if(intent.extras!!.getBoolean("isIdenedi",false)) {
                if (str.contains("code=")) {
                    str.substring(0, str.lastIndexOf("/"))
                    val code = str.substring(str.indexOf("=") + 1, str.lastIndexOf("&"))
                    ESP_LIB_CustomLogs.displayLogs("$TAG code: $code")

                    // web_view.loadUrl("https://app.idenedi.com/authorization/?response_type=code&client_id=XSP1980031200&redirect_uri=https://qaesp.azurewebsites.net&&state")
                    if (!isIdenediServiceRunning) {
                        isIdenediServiceRunning = true
                        pref?.saveIdenediCode(code)
                        finish()
                    }
                } else if (str.contains("access_denied")) {
                    finish()
                } else if (str.contains("error=")) {
                    ESP_LIB_CustomLogs.displayLogs("$TAG Error")
                }
            }


        }
    }

    private fun start_loading_animation() {
        try {
            if (!pDialog!!.isShowing())
                pDialog!!.show()
        } catch (e: java.lang.Exception) {
        }

    }

    private fun stop_loading_animation() {
        try {
            if (pDialog!!.isShowing())
                pDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }


    companion object {

        var ACTIVITY_NAME = "controllers.WebViewScreenActivity"
    }

}
