package com.esp.library.exceedersesp.controllers.signature

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_GoogleFontsLibrary
import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO
import kotlinx.android.synthetic.main.esp_lib_activity_my_signature.*
import kotlinx.android.synthetic.main.esp_lib_criteriasignature.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import retrofit2.Call
import retrofit2.Response

class ESP_LIB_My_Signature : ESP_LIB_BaseActivity() {


    internal var pDialog: AlertDialog? = null
    var body: ESP_LIB_SignatureDAO? = null

    var rawStyles = intArrayOf(R.raw.caveat_regular, R.raw.dancingscript, R.raw.gloriagallelujah_regular, R.raw.greatvibes_regular, R.raw.indieflower_regular,
            R.raw.kaushanscript_regular, R.raw.pacifico_regular, R.raw.pangolin_regular, R.raw.parisienne_regular, R.raw.rocksalt_regular,
            R.raw.sacramento_regular)

    var rawStylesNames = arrayOf("caveat_regular", "dancingscript", "gloriagallelujah_regular", "greatvibes_regular", "indieflower_regular",
            "kaushanscript_regular", "pacifico_regular", "pangolin_regular", "parisienne_regular", "rocksalt_regular",
            "sacramento_regular")

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ESP_LIB_ESPApplication.getInstance().applicationTheme)
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_my_signature)
        initailize()

        txtdescription.text = getString(R.string.app_name) + " " + getString(R.string.esp_lib_text_mysignature_description)

        btcustomize.setOnClickListener {
            val i = Intent(this, ESP_LIB_Customize_My_Signature::class.java)
            i.putExtra("body", body);
            startActivity(i)
        }


    }

    private fun initailize() {
        setSupportActionBar(gradienttoolbar)
        ibToolbarBack.setOnClickListener { onBackPressed() }
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(this)
        val mySignatureText = getString(R.string.esp_lib_text_mysignature)
        toolbarheading.text = mySignatureText
        etxtsignLabel.hint = mySignatureText
    }

    fun getSignature() {

        var apiResponse: Response<ESP_LIB_SignatureDAO>? = null
        start_loading_animation()
        try {

            val call_upload = ESP_LIB_Shared.getInstance().retroFitObject(this).getSignature()
            call_upload.enqueue(object : retrofit2.Callback<ESP_LIB_SignatureDAO> {


                override fun onResponse(call: Call<ESP_LIB_SignatureDAO>?, response: Response<ESP_LIB_SignatureDAO>?) {
                    if (response?.code() == 200) {
                        populateData(response)
                    }

                    stop_loading_animation()

                }


                override fun onFailure(call: Call<ESP_LIB_SignatureDAO>?, t: Throwable?) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)

                }


            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)

        }
    }


    private fun populateData(response: Response<ESP_LIB_SignatureDAO>?) {
        if(response?.body() != null) {
            body = response.body()
            if (body?.type.equals(getString(R.string.esp_lib_text_font))) {
               setFontText(body?.signatoryName,body?.fontFamily)
            } else {
                rlincludesignatue.visibility = View.GONE
                llsignature.visibility = View.VISIBLE

                Glide.with(this).load(body?.file?.downloadUrl)
                        .error(R.drawable.esp_lib_drawable_default_profile_picture)
                        .into(ivsignature)
            }
        }
        else{
            setFontText(ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.name,null)
        }
    }

    private fun setFontText(signatoryName: String?, fontFamily: String?)
    {
        rlincludesignatue.visibility = View.VISIBLE
        llsignature.visibility = View.GONE
        etxtsign.setText(signatoryName)
        val indexOf = rawStylesNames.indexOf(fontFamily)
        if (indexOf == -1) {
            val typeface = Typeface.createFromAsset(assets, "font/greatvibes_regular.otf")
            etxtsign.setTypeface(typeface)
        } else {
            etxtsign.setTypeface(ESP_LIB_GoogleFontsLibrary.setGoogleFont(this, rawStyles[indexOf]))
        }
    }

    private fun start_loading_animation() {

        if (!pDialog!!.isShowing())
            pDialog?.show()


    }

    private fun stop_loading_animation() {

        if (pDialog!!.isShowing())
            pDialog?.dismiss()


    }

    override fun onResume() {
        super.onResume()
        when (ESP_LIB_Shared.getInstance().isWifiConnected(this)) {
            true -> getSignature()
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), this)
        }
    }
}