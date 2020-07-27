package com.esp.library.exceedersesp.controllers.Profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.ESP_LIB_Constants
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.exceedersesp.controllers.Profile.adapters.ESP_LIB_ListofSectionsAdapter
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.esp_lib_section_detail_screen.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.addapplication.ESP_LIB_CurrencyDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO

class ESP_LIB_SectionDetailScreen : ESP_LIB_BaseActivity() {

    internal var TAG = javaClass.simpleName

    internal var ischeckerror: Boolean = false
    internal var context: Context? = null
    internal var ESPLIBDynamicFormSectionDAO: ESP_LIB_DynamicFormSectionDAO? = null
    internal var adapterESPLIB: ESP_LIB_ListofSectionsAdapter? = null
    internal var pDialog: AlertDialog? = null
    var mApplicationLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_section_detail_screen)
        initialize()

        ischeckerror = intent.getBooleanExtra("ischeckerror", false)

        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            loadCurrencies()
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }

        add_more.setOnClickListener { addCard() }

        txtcancel.setOnClickListener { onBackPressed() }

    }


    private fun initialize() {
        context = this@ESP_LIB_SectionDetailScreen
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        app_list.setHasFixedSize(true)
        app_list.layoutManager = mApplicationLayoutManager
        app_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        ESPLIBDynamicFormSectionDAO = intent.getSerializableExtra("data") as ESP_LIB_DynamicFormSectionDAO
        txtheader.text = ESPLIBDynamicFormSectionDAO?.defaultName

        if (ESPLIBDynamicFormSectionDAO!!.isMultipule)
            lladdmore.visibility = View.VISIBLE
        else
            lladdmore.visibility = View.GONE

    }

    private fun getSections() {
        val dataapplicant = intent.getSerializableExtra("dataapplicant") as ESP_LIB_ApplicationProfileDAO
        val sectionsFields = ESPLIBDynamicFormSectionDAO!!.fields

        adapterESPLIB = ESPLIBDynamicFormSectionDAO?.let { context?.let { it1 -> sectionsFields?.let { it2 ->
            ESP_LIB_ListofSectionsAdapter(it, dataapplicant, it2, it1, ischeckerror) } } }
        app_list.adapter = adapterESPLIB
    }

    private fun addCard() {
        val dataapplicant = intent.getSerializableExtra("dataapplicant") as ESP_LIB_ApplicationProfileDAO
        val i = Intent(context, ESP_LIB_EditSectionDetails::class.java)
        i.putExtra("data", ESPLIBDynamicFormSectionDAO)
        i.putExtra("dataapplicant", dataapplicant)
        i.putExtra("ischeckerror", ischeckerror)
        i.putExtra("isprofile", intent.getBooleanExtra("isprofile", false))
        i.putExtra("isaddmore", true)
        startActivity(i)
    }

    fun loadCurrencies() {
        start_loading_animation()
        try {
            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

            val call = apis.getCurrency()

            call.enqueue(object : Callback<List<ESP_LIB_CurrencyDAO>> {
                override fun onResponse(call: Call<List<ESP_LIB_CurrencyDAO>>, response: Response<List<ESP_LIB_CurrencyDAO>>) {

                    if (response.body() != null && response.body().size > 0) {
                        ESP_LIB_ESPApplication.getInstance().currencies = response.body()
                    }
                    stop_loading_animation()
                    getSections()
                }

                override fun onFailure(call: Call<List<ESP_LIB_CurrencyDAO>>, t: Throwable) {
                    stop_loading_animation()
                }
            })

        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
        }

    }

    private fun start_loading_animation() {
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun stop_loading_animation() {
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }
}
