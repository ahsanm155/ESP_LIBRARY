package com.esp.library.exceedersesp.controllers.applications

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.ESP_LIB_Constants
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.common.FullScreenDialogExample
import com.esp.library.utilities.customevents.EventOptions
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.esp_lib_activity_reassign_users_list.*
import kotlinx.android.synthetic.main.esp_lib_activity_search_layout.*
import kotlinx.android.synthetic.main.esp_lib_gradientcurvetoolbar.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.adapters.setup.applications.ESP_LIB_ListUsersAdapter
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.ESP_LIB_UsersListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaCommentsListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.interfaces.ESP_LIB_UserListClickListener
import java.util.concurrent.TimeUnit

class ESP_LIB_ReassignUsersList : ESP_LIB_BaseActivity(), ESP_LIB_UserListClickListener {


    internal var context: ESP_LIB_BaseActivity? = null
    internal var pDialog: AlertDialog? = null
    var pref: ESP_LIB_SharedPreference? = null
    var dynamicStagesCriteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO? = null
    var userAdapterESPLIB: ESP_LIB_ListUsersAdapter? = null
    val userList = ArrayList<ESP_LIB_UsersListDAO>()
    internal var searchView: SearchView? = null
    internal var myActionMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_reassign_users_list)
        initailize()


        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            loadUsersList()
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }

        etxtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(query: CharSequence, start: Int, before: Int, count: Int) {
                if (query.isEmpty()) {
                    userAdapterESPLIB = ESP_LIB_ListUsersAdapter(userList, context!!, "", rvUsersList)
                    rvUsersList.adapter = userAdapterESPLIB
                } else
                    userAdapterESPLIB?.filter?.filter(query);
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        etxtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(search_text: CharSequence, start: Int, before: Int, count: Int) {
                if (search_text.isEmpty())
                    ibClear.visibility = View.GONE
                else
                    ibClear.visibility = View.VISIBLE
            }

            override fun afterTextChanged(search_text: Editable) {
            }
        })

        etxtsearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ESP_LIB_Shared.getInstance().hideKeyboard(context)
                    return true
                }
                return false
            }
        })


        ibClear.setOnClickListener {
            etxtsearch.setText("")
            /* isEventRefreshData = true
             loadApplications(false, "")*/
        }

        btreassign.setOnClickListener {
            if (btreassign.text.toString().equals(getString(R.string.esp_lib_text_reassign), ignoreCase = true)) {
                rlreassignform.visibility = View.GONE
                rlreasonform.visibility = View.VISIBLE
                btreassign.text = getString(R.string.esp_lib_text_submit)
                headingtext.text = getString(R.string.esp_lib_text_reassign_title)
            } else {
                if (userAdapterESPLIB?.getList() != null) {
                    for (element in userAdapterESPLIB?.getList()!!) {
                        if (element.isChecked) {
                            if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                                if (!etxtcomment.text.isNullOrEmpty()) {
                                    val commentList = ArrayList<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>()
                                    val dynamicStagesCriteriaCommentsListDAO = ESP_LIB_DynamicStagesCriteriaCommentsListDAO()
                                    dynamicStagesCriteriaCommentsListDAO.comment = etxtcomment.text.toString()
                                    commentList.add(dynamicStagesCriteriaCommentsListDAO)
                                    dynamicStagesCriteriaListDAO?.comments = commentList
                                }
                                sendReAssignData(dynamicStagesCriteriaListDAO, element.id)

                            } else {
                                ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
                            }

                            break
                        }
                    }
                }

            }

        }
        btcancel.setOnClickListener { onBackPressed() }


    }

    private fun initailize() {
        context = this@ESP_LIB_ReassignUsersList
        pref = ESP_LIB_SharedPreference(context)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        setSupportActionBar(gradientcurvetoolbar)
        supportActionBar?.title = ""
        ibToolbarBack.setOnClickListener { onBackPressed() }
        headingtext.text = getString(R.string.esp_lib_text_reassignto)

        dynamicStagesCriteriaListDAO = intent.getSerializableExtra("criteriaListDAO") as ESP_LIB_DynamicStagesCriteriaListDAO

        rvUsersList.setHasFixedSize(true)
        rvUsersList.isNestedScrollingEnabled = false
        val linearLayoutManagerCrteria = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        rvUsersList.layoutManager = linearLayoutManagerCrteria

    }


    fun loadUsersList() {

        start_loading_animation()
        try {

            var call = ESP_LIB_Shared.getInstance().retroFitObject(context).getUser()

            call.enqueue(object : Callback<List<ESP_LIB_UsersListDAO>> {
                override fun onResponse(call: Call<List<ESP_LIB_UsersListDAO>>, response: Response<List<ESP_LIB_UsersListDAO>>) {


                    if (response.body() != null && response.body().size > 0) {

                        txtnorecords.visibility = View.GONE
                        rvUsersList.visibility = View.VISIBLE
                        val body = response.body()

                        for (i in 0 until body.size) {
                            if (dynamicStagesCriteriaListDAO?.ownerId != body.get(i).id)
                                userList.add(body.get(i))
                        }

                        userAdapterESPLIB = ESP_LIB_ListUsersAdapter(userList, context!!, "", rvUsersList)
                        rvUsersList.adapter = userAdapterESPLIB


                    } else {

                        txtnorecords.visibility = View.VISIBLE
                        rvUsersList.visibility = View.GONE
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

    fun sendReAssignData(post: ESP_LIB_DynamicStagesCriteriaListDAO?, ownerId: Int?) {

        start_loading_animation()

        try {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()


            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url()

                val url = originalHttpUrl.newBuilder()
                        .addQueryParameter("applicationId", ESP_LIB_ApplicationSingleton.instace.application?.applicationId.toString())
                        .addQueryParameter("criterionId", post?.id.toString())
                        .addQueryParameter("assessmentId", post?.assessmentId.toString())
                        .addQueryParameter("newOwnerId", ownerId.toString())
                        .build()

                var access_token: String? = null
                if (ESP_LIB_ESPApplication.getInstance().access_token != null) access_token = ESP_LIB_ESPApplication.getInstance().access_token else if (ESP_LIB_ESPApplication.getInstance()?.user != null && ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse != null) access_token = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.access_token

                val requestBuilder = original.newBuilder()
                        .url(url)
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguage(context))
                        .header("Authorization", "bearer " + access_token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            if (ESP_LIB_Constants.WRITE_LOG) {
                httpClient.addInterceptor(logging)
            }

            httpClient.connectTimeout(2, TimeUnit.MINUTES)
            httpClient.readTimeout(2, TimeUnit.MINUTES)
            httpClient.writeTimeout(2, TimeUnit.MINUTES)

            /*Gson object for custom field types*/
            /*  val gson = GsonBuilder()
                      .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                      .create()*/

            val gson = GsonBuilder()
                    .setExclusionStrategies(object : ExclusionStrategy {
                        override fun shouldSkipField(f: FieldAttributes): Boolean {
                            return f.getDeclaredClass().equals(ESP_LIB_DynamicStagesCriteriaListDAO::class.java)
                        }

                        override fun shouldSkipClass(clazz: Class<*>): Boolean {
                            return false
                        }
                    })
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            /* retrofit builder and call web service*/
            var retrofit = Retrofit.Builder()
                    .baseUrl(ESP_LIB_Constants.base_url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()

            /* APIs Mapping respective Object*/
            val apis = retrofit.create(ESP_LIB_APIs::class.java)


            //  var dynamicStagesCriteriaListDAO=setValues(post)


            var call = apis.reAssignData(post)

            call.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    stop_loading_animation()


                    val dialogFragment = FullScreenDialogExample()
                    val args = Bundle()
                    args.putString("message", getString(R.string.esp_lib_text_reassign_criteria_success))
                    dialogFragment.arguments = args
                    dialogFragment.show(supportFragmentManager, "popup")


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

    override fun userClick(userslistDAOESPLIB: ESP_LIB_UsersListDAO?) {
        var isUserSelected = false
        if (userAdapterESPLIB?.getList() != null) {
            for (element in userAdapterESPLIB?.getList()!!) {
                if (element.isChecked) {
                    isUserSelected = true
                    break
                }
            }
        }

        if (isUserSelected) {
            btreassign.isEnabled = true
            btreassign.alpha = 1.0f
            btreassign.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green)
        } else {
            btreassign.isEnabled = false
            btreassign.alpha = 0.5f
            btreassign.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button)
        }


        /* if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            sendReAssignData(dynamicStagesCriteriaListDAO, userslistDAOESPLIB?.id)
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }*/
    }

    override fun onBackPressed() {

        if (rlreassignform.visibility == View.GONE) {
            rlreassignform.visibility = View.VISIBLE
            rlreasonform.visibility = View.GONE
            btreassign.text = getString(R.string.esp_lib_text_reassign)
            headingtext.text = getString(R.string.esp_lib_text_reassignto)
        } else {
            ESP_LIB_ActivityStageDetails.isGoBAck = false
            super.onBackPressed()
        }
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
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onStart() {
        super.onStart()
        registerReciever()
    }

    override fun onStop() {
        super.onStop()
        unRegisterReciever()
    }

    private fun registerReciever() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    private fun unRegisterReciever() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun popupismissEvent(popUpDismiss: EventOptions.PopUpDismissEvent) {
        ESP_LIB_ActivityStageDetails.isGoBAck = true
        finish()

    }

}
