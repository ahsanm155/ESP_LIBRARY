package com.esp.library.exceedersesp.controllers.lookupinfo

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.ESP_LIB_Constants
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.lookupinfo.adapter.ESP_LIB_ListLookupInfoItemsDetailAdapter
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import kotlinx.android.synthetic.main.esp_lib_lookup_item_detail.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldLookupValuesDAO
import utilities.data.lookup.ESP_LIB_LookupItemDetailDAO
import java.util.*
import java.util.concurrent.TimeUnit

class ESP_LIB_LookupItemDetail : ESP_LIB_BaseActivity() {
    internal var context: ESP_LIB_BaseActivity? = null
    internal var TAG = javaClass.simpleName
    private var lookupItemDetailLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var pDialog: AlertDialog? = null
    internal var pref: ESP_LIB_SharedPreference? = null

    internal var lookupInfoDetailItemArray = ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_lookup_item_detail)
        initialize()
        getData()

        swipeRefreshLayout!!.setOnRefreshListener { getData() }

     /*   lookup_item_detail_list?.addOnScrollListener(object : EndlessRecyclerViewScrollListener(lookupItemDetailLayoutManager as LinearLayoutManager?) {
            override fun onHide() {
                Shared.getInstance().setToolbarHeight(toolbar, false)
            }

            override fun onShow() {
                Shared.getInstance().setToolbarHeight(toolbar, true)
            }

            override fun getFooterViewType(defaultNoFooterViewType: Int): Int {
                return defaultNoFooterViewType
            }

            override fun onLoadMore(page: Int, totalItemsCount: Int) {


            }

        })*/


    }

    private fun initialize() {
        context = this@ESP_LIB_LookupItemDetail
        setSupportActionBar(gradienttoolbar)
        supportActionBar?.title = ""
        ibToolbarBack.setOnClickListener { onBackPressed() }
        toolbarheading.text = intent.getStringExtra("toolbar_heading")

        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        pref = ESP_LIB_SharedPreference(context)

        lookupItemDetailLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        lookup_item_detail_list.setHasFixedSize(true)
        lookup_item_detail_list.layoutManager = lookupItemDetailLayoutManager
        lookup_item_detail_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        val themeColor = ContextCompat.getColor(context!!,R.color.colorPrimaryDark)
        swipeRefreshLayout?.setColorSchemeColors(themeColor, themeColor, themeColor)

    }

    private fun getData() {
        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            getLookupItemDetail()
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext)
        }
    }

    fun getLookupItemDetail() {
        start_loading_animation()
        try {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguage(context))
                        .header("Authorization", "bearer " + ESP_LIB_ESPApplication.getInstance().user.loginResponse?.access_token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            httpClient.connectTimeout(10, TimeUnit.SECONDS)
            httpClient.readTimeout(10, TimeUnit.SECONDS)
            httpClient.writeTimeout(10, TimeUnit.SECONDS)

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            val retrofit = Retrofit.Builder()
                    .baseUrl(ESP_LIB_Constants.base_url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())

                    .build()


            val apis = retrofit.create(ESP_LIB_APIs::class.java)


            val detail_call = apis.getLookupItemDetail(intent.getIntExtra("id", 0))
            detail_call.enqueue(object : Callback<ESP_LIB_LookupItemDetailDAO> {
                override fun onResponse(call: Call<ESP_LIB_LookupItemDetailDAO>, response: Response<ESP_LIB_LookupItemDetailDAO>) {
                    lookupInfoDetailItemArray.clear()
                    stop_loading_animation()
                    var value: String
                    val body = response.body()

                    if (body != null) {
                        var sections = body.lookup.form.sections

                        for (i in sections.indices) {
                            val dynamicFormSectionDAO = sections[i]
                            val fields = dynamicFormSectionDAO.fields
                            fields?.let { lookupInfoDetailItemArray.addAll(it) }
                            for (j in fields!!.indices) {
                                val dynamicFormSectionFieldDAO = fields?.get(j)

                                val sectionCustomFieldId = dynamicFormSectionFieldDAO.sectionCustomFieldId
                                val label = dynamicFormSectionFieldDAO.label
                                value = dynamicFormSectionFieldDAO.value!!
                                val lookupValues = dynamicFormSectionFieldDAO.lookupValues

                                val values = body.values

                                for (k in values.indices) {
                                    val dynamicFormValuesDAO = values[k]

                                    val sectionCustomValueFieldId = dynamicFormValuesDAO.sectionCustomFieldId
                                    if (sectionCustomValueFieldId == sectionCustomFieldId) {

                                        val customFieldLookupId = dynamicFormValuesDAO.customFieldLookupId
                                        val type = dynamicFormValuesDAO.type

                                        if (customFieldLookupId < 0 && type == 13) {
                                         if(dynamicFormValuesDAO.selectedLookupText != null)
                                            value = dynamicFormValuesDAO.selectedLookupText!!
                                        }
                                        else
                                            value = dynamicFormValuesDAO.value!!

                                        if (type == 5) {
                                            value = getSingleSelectionValue(value, lookupValues!!)
                                        } else if (type == 6) {
                                            value = getMultiSectionText(value, lookupValues!!)
                                        } else if (type == 7) {
                                            val details = dynamicFormValuesDAO.details
                                            if (details != null) {
                                                val fieldDetails = ESP_LIB_DyanmicFormSectionFieldDetailsDAO()
                                                fieldDetails.name = details.name
                                                fieldDetails.mimeType = details.mimeType
                                                fieldDetails.createdOn = details.createdOn
                                                fieldDetails.downloadUrl = details.downloadUrl
                                                dynamicFormSectionFieldDAO.details = fieldDetails
                                            }
                                        } else if (type == 13) {
                                            dynamicFormValuesDAO.selectedLookupText?.let {
                                                value = it
                                            }
                                        }
                                        dynamicFormSectionFieldDAO.value = value
                                        lookupInfoDetailItemArray[j] = dynamicFormSectionFieldDAO

                                    }
                                }
                            }
                        }

                        val dynamicFormSectionFieldDAOS = invisibleList(lookupInfoDetailItemArray)
                        val adapter = ESP_LIB_ListLookupInfoItemsDetailAdapter(dynamicFormSectionFieldDAOS, context!!)
                        lookup_item_detail_list.adapter = adapter
                    } else {
                        stop_loading_animation()
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    }

                }

                override fun onFailure(call: Call<ESP_LIB_LookupItemDetailDAO>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    return
                }
            })

        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)


        }

    }

    private fun invisibleList(fieldsList: ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>): List<ESP_LIB_DynamicFormSectionFieldDAO> {
        val tempFields = ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>()
        for (h in fieldsList.indices) {
            if (fieldsList[h].isVisible && !fieldsList[h].label.equals(ESP_LIB_ESPApplication.getInstance().user.loginResponse?.role?.toLowerCase(), ignoreCase = true)) {
                tempFields.add(fieldsList[h])
            }
        }
        return tempFields
    }

    private fun getSingleSelectionValue(value: String, lookupValueESPLIBS: List<ESP_LIB_DynamicFormSectionFieldLookupValuesDAO>): String {
        var value = value
        val digitsOnly = TextUtils.isDigitsOnly(value)
        if (digitsOnly) {
            for (h in lookupValueESPLIBS.indices) {
                val dynamicFormSectionFieldLookupValuesDAO = lookupValueESPLIBS[h]
                try {
                    if (dynamicFormSectionFieldLookupValuesDAO.id == Integer.parseInt(value))
                        value = dynamicFormSectionFieldLookupValuesDAO.label!!
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return value
    }

    private fun getMultiSectionText(value: String, lookupValueESPLIBS: List<ESP_LIB_DynamicFormSectionFieldLookupValuesDAO>): String {
        val sb = StringBuilder()
        val split = value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (m in split.indices) {
            for (h in lookupValueESPLIBS.indices) {
                val dynamicFormSectionFieldLookupValuesDAO = lookupValueESPLIBS[h]
                try {
                    if (dynamicFormSectionFieldLookupValuesDAO.id == Integer.parseInt(split[m])) {
                        val label1 = dynamicFormSectionFieldLookupValuesDAO.label

                        val getsb = getJSONObjectValue(label1!!, split, m)

                        if (getsb.replace("\\s".toRegex(), "").length == 0) {
                            sb.append(label1)
                            if (split.size - 1 != m)
                                sb.append(", ")
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return sb.toString()
    }

    private fun getJSONObjectValue(label: String, split: Array<String>, m: Int): String {
        val sb = StringBuilder()
        if (ESP_LIB_Shared.getInstance().isJSONValid(label)) {
            try {
                val jsonObject = JSONObject(label)
                if (jsonObject.has(pref?.language)) {
                    val text = jsonObject.getString(pref?.language)
                    sb.append(text)
                    if (split.size - 1 != m)
                        sb.append(",")
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return sb.toString()
    }

    private fun start_loading_animation() {
        swipeRefreshLayout!!.isRefreshing = true
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun stop_loading_animation() {
        swipeRefreshLayout!!.isRefreshing = false
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }

}
