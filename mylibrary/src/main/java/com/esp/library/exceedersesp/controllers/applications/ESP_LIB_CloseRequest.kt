package com.esp.library.exceedersesp.controllers.applications


import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.customevents.EventOptions.EventRefreshData
import com.esp.library.utilities.data.applicants.ESP_LIB_CancelApplicationDAO
import kotlinx.android.synthetic.main.esp_lib_activity_close_request.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.applications.ESP_LIB_ListUsersSelectionAdapter
import utilities.data.applicants.ESP_LIB_UsersListDAO

class ESP_LIB_CloseRequest : ESP_LIB_BaseActivity() {


    val userList = ArrayList<ESP_LIB_UsersListDAO>()
    var userAdapterESPLIB: ESP_LIB_ListUsersSelectionAdapter? = null
    var context: ESP_LIB_BaseActivity? = null
    internal var pDialog: AlertDialog? = null
    var pref: ESP_LIB_SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ESP_LIB_ESPApplication.getInstance().applicationTheme)
        changeStatusBarColor(true)
        setContentView(R.layout.esp_lib_activity_close_request)
        initialize()
        loadUsersList()

        txtreason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) {
                    btcancelrequest.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button)
                    btcancelrequest.alpha = 0.5f
                    btcancelrequest.isEnabled = false
                } else {
                    btcancelrequest.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green)
                    btcancelrequest.alpha = 1.0f
                    btcancelrequest.isEnabled = true
                }
            }

            override fun afterTextChanged(search_text: Editable) {}
        })

        btcancel.setOnClickListener { onBackPressed() }

        btcancelrequest.setOnClickListener {

            var notifyApplicant = false

            val personaIds = ArrayList<Int>()
            userAdapterESPLIB?.getUserList()?.forEach {
                if (it.isChecked) {
                    if (it.id == -1122)
                        notifyApplicant = true
                    else
                        personaIds.add(it.id)
                }
            }
            val esp_lib_cancelApplicationDAO = ESP_LIB_CancelApplicationDAO()
            esp_lib_cancelApplicationDAO.applicationid = intent.getIntExtra("applicationId", 0)
            esp_lib_cancelApplicationDAO.comments = txtreason.text.toString()
            esp_lib_cancelApplicationDAO.notifyApplicant = notifyApplicant
            esp_lib_cancelApplicationDAO.notifiedToPersonaIds = personaIds

            cancelRequest(esp_lib_cancelApplicationDAO)
        }


    }


    private fun initialize() {
        context = this
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(bContext)
        pref = ESP_LIB_SharedPreference(context)
        setSupportActionBar(gradienttoolbar)
        supportActionBar?.title = ""
        ibToolbarBack.setOnClickListener { onBackPressed() }
        toolbarheading.text = getString(R.string.esp_lib_text_close_request)
        btcancelrequest.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button)
        btcancelrequest.alpha = 0.5f
        btcancelrequest.isEnabled = false
        rvUsersList.setHasFixedSize(true)
        rvUsersList.isNestedScrollingEnabled = false
        val linearLayoutManagerCrteria = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        rvUsersList.layoutManager = linearLayoutManagerCrteria
        val dividerItemDecoration = androidx.recyclerview.widget.DividerItemDecoration(rvUsersList.getContext(),
                linearLayoutManagerCrteria.getOrientation())
        rvUsersList.addItemDecoration(dividerItemDecoration)

        userAdapterESPLIB = ESP_LIB_ListUsersSelectionAdapter(userList, context!!)
        rvUsersList.adapter = userAdapterESPLIB
    }

    fun cancelRequest(espLibCancelapplicationdao: ESP_LIB_CancelApplicationDAO) {

        start_loading_animation()
        try {

            val apis = CompRoot().getService(context);
            val call = apis?.cancelRequestData(espLibCancelapplicationdao)
            call?.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {


                    if (response.code() == 500) {
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    } else {
                        EventBus.getDefault().post(EventRefreshData())
                        onBackPressed()
                    }
                    stop_loading_animation()

                }


                override fun onFailure(call: Call<Any>, t: Throwable) {
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    stop_loading_animation()

                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()

            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)

        }

    }


    fun loadUsersList() {

        start_loading_animation()
        try {

            val apis = CompRoot().getService(context);
            val call = apis?.getUsersList(intent.getIntExtra("applicationId", 0))
            call?.enqueue(object : Callback<List<ESP_LIB_UsersListDAO>> {
                override fun onResponse(call: Call<List<ESP_LIB_UsersListDAO>>, response: Response<List<ESP_LIB_UsersListDAO>>) {


                    if (response.body() != null && response.body().size > 0) {

                        val body = response.body()
                        addDefaultValueInArray()
                        userList.addAll(body)
                        userAdapterESPLIB?.notifyDataSetChanged()

                    } else {

                        addDefaultValueInArray()
                        userAdapterESPLIB?.notifyDataSetChanged()
                    }
                    stop_loading_animation()

                }


                override fun onFailure(call: Call<List<ESP_LIB_UsersListDAO>>, t: Throwable) {
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    stop_loading_animation()

                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()

            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)

        }

    }

    private fun addDefaultValueInArray()
    {
        val esp_LIB_UsersListDAO = ESP_LIB_UsersListDAO()
        esp_LIB_UsersListDAO.id = -1122
        esp_LIB_UsersListDAO.isChecked = true
        esp_LIB_UsersListDAO.fullName = getString(R.string.esp_lib_text_employee)
        userList.add(esp_LIB_UsersListDAO)
    }


    private fun start_loading_animation() {
        try {
            if (!pDialog!!.isShowing())
                pDialog?.show()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun stop_loading_animation() {

        try {
            if (pDialog!!.isShowing())
                pDialog?.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }


}
