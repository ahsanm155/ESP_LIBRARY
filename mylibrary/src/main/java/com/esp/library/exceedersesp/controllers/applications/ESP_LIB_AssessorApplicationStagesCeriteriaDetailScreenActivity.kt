package com.esp.library.exceedersesp.controllers.applications

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.*

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_AssesscorStagesCriteriaDetailFragment
import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import kotlinx.android.synthetic.main.esp_lib_activity_assessor_application_stages__criteria_detail.*
import kotlinx.android.synthetic.main.esp_lib_activity_assessor_application_stages__criteria_detail.definitionName
import kotlinx.android.synthetic.main.esp_lib_activity_assessor_application_stages__criteria_detail.toolbar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.adapters.setup.applications.ESP_LIB_ListStagesCriteriaDetailAdapter
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.addapplication.ESP_LIB_PostApplicationsStatusDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO

class ESP_LIB_AssessorApplicationStagesCeriteriaDetailScreenActivity : ESP_LIB_BaseActivity(), ESP_LIB_ListStagesCriteriaDetailAdapter.CriteriaStatusChange {
    internal var context: ESP_LIB_BaseActivity? = null
    internal var detail_call: Call<ESP_LIB_DynamicResponseDAO>? = null
    internal var status_call: Call<Int>? = null
    internal var anim: ESP_LIB_ProgressBarAnimation? = null
    internal var mStage: ESP_LIB_DynamicStagesDAO? = null
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var submit_request: ESP_LIB_AssesscorStagesCriteriaDetailFragment? = null
    internal var imm: InputMethodManager? = null
    internal var pref: ESP_LIB_SharedPreference? = null

    internal var pDialog: android.app.AlertDialog? = null

    override fun StatusChange(update: ESP_LIB_DynamicStagesCriteriaListDAO, isAccepted: Boolean) {

        val post = ESP_LIB_PostApplicationsStatusDAO()
        post.isAccepted = isAccepted
        post.applicationId = ESP_LIB_ApplicationSingleton.instace.application!!.applicationId
        post.assessmentId = update.assessmentId
        post.comments = ""
        post.stageId = update.stageId

        var button_status = ""

        if (isAccepted) {
            button_status = getString(R.string.esp_lib_text_acceptcriteri)
        } else {
            button_status = getString(R.string.esp_lib_text_rejectcriteria)
        }

        DialogStatusChanged(post, button_status)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_assessor_application_stages__criteria_detail)
        initailize()
        setGravity()
        val bnd = intent.extras
        if (bnd != null) {
            mStage = bnd.getSerializable(ESP_LIB_DynamicStagesDAO.BUNDLE_KEY) as ESP_LIB_DynamicStagesDAO
            if (mStage != null) {
                //GetApplicationDetail(mStage.getId()+"");
                UpdateTopView()
            }
        }
        try {
            ESP_LIB_Shared.getInstance().createFolder(ESP_LIB_Constants.FOLDER_PATH, ESP_LIB_Constants.FOLDER_NAME, context)
        } catch (e: Exception) {
        }


        fm = this.context!!.getSupportFragmentManager()
        submit_request = mStage?.let { ESP_LIB_AssesscorStagesCriteriaDetailFragment.newInstance(it) }
        val ft = fm!!.beginTransaction()
        ft.add(R.id.request_fragment, submit_request!!)
        ft.commit()

    }

    private fun initailize() {
        context = this@ESP_LIB_AssessorApplicationStagesCeriteriaDetailScreenActivity
        pref = ESP_LIB_SharedPreference(context!!)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setTitle("")
        toolbar.navigationIcon = ContextCompat.getDrawable(this,R.drawable.esp_lib_drawable_ic_arrow_nav_back)
        toolbar.navigationIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        toolbar.setNavigationOnClickListener { v -> finish() }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun start_loading_animation() {
        appbar.visibility = View.GONE
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun stop_loading_animation() {
        appbar.visibility = View.VISIBLE
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }

    fun GetApplicationDetail() {

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

            if (ESP_LIB_ApplicationSingleton.instace.application != null) {
                detail_call = apis.GetApplicationDetailv2(ESP_LIB_ApplicationSingleton.instace.application!!.applicationId.toString() + "",
                false,false)
            }


            detail_call!!.enqueue(object : Callback<ESP_LIB_DynamicResponseDAO> {
                override fun onResponse(call: Call<ESP_LIB_DynamicResponseDAO>, responseESPLIB: Response<ESP_LIB_DynamicResponseDAO>?) {

                    stop_loading_animation()

                    if (responseESPLIB != null && responseESPLIB.body() != null) {

                        if (ESP_LIB_ApplicationSingleton.instace.application != null) {
                            ESP_LIB_ApplicationSingleton.instace.application = null
                        }
                        ESP_LIB_ApplicationSingleton.instace.application = responseESPLIB.body()



                        if (submit_request != null) {
                            submit_request!!.UpdateCriteriaList(submit_request!!.view)
                        }

                    }
                }

                override fun onFailure(call: Call<ESP_LIB_DynamicResponseDAO>, t: Throwable) {
                    stop_loading_animation()
                    t.printStackTrace()
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    return
                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ex.printStackTrace()
            ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
            return
        }

    }

    fun AcceptOrReject(ESPLIBPost: ESP_LIB_PostApplicationsStatusDAO) {

        val view = currentFocus
        if (view != null) {
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }

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

            status_call = apis.AcceptRejectApplication(ESPLIBPost)


            status_call!!.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>?) {


                    if (response != null && response.body() != null && response.body() > 0) {
                        ESP_LIB_Constants.isApplicationChagned = true
                        GetApplicationDetail()
                    } else
                        stop_loading_animation()
                }

                override fun onFailure(call: Call<Int>, t: Throwable?) {
                    stop_loading_animation()
                    if (t != null && context != null) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    }
                }
            })

        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            if (context != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
            }

        }

    }

    private fun UpdateTopView() {
        if (mStage != null) {
            category.text = mStage?.name
            definitionName.setText(R.string.esp_lib_text_criterias)
            val status = mStage?.status
            ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME+" status: "+status)
        }
    }

    fun DialogStatusChanged(ESPLIBPost: ESP_LIB_PostApplicationsStatusDAO, button_label: String) {

        val builder = AlertDialog.Builder(context!!)


        val view = LayoutInflater.from(context).inflate(R.layout.esp_lib_dialog_layout, null)
        val input = view.findViewById<EditText>(R.id.reason)
        builder.setView(view)

        if (button_label == getString(R.string.esp_lib_text_acceptcriteri)) {
            builder.setTitle(R.string.esp_lib_text_accept_title)
            input.hint = getString(R.string.esp_lib_text_addcomment)
            view.visibility = View.VISIBLE
        } else {
            builder.setTitle(R.string.esp_lib_text_reject_title)
            input.hint = getString(R.string.esp_lib_text_reject_comments_hints_one_comment) + " " + pref?.getlabels()?.application + " " + getString(R.string.esp_lib_text_beforereject) + " " + pref?.getlabels()?.application
            view.visibility = View.VISIBLE
        }


        builder.setPositiveButton(button_label) { dialog, which ->
            val m_Text = input.text.toString()

            if (m_Text != null && m_Text.length > 0) {
                ESPLIBPost.comments = m_Text

            }

            imm!!.hideSoftInputFromWindow(input.windowToken, 0)
            AcceptOrReject(ESPLIBPost)
            dialog.cancel()
        }



        builder.setNegativeButton(getString(R.string.esp_lib_text_cancel)) { dialog, which ->
            imm!!.hideSoftInputFromWindow(input.windowToken, 0)
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context!!.getResources().getColor(R.color.esp_lib_color_black))

        if (button_label == getString(R.string.esp_lib_text_acceptcriteri)) {
            return
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        input.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                       count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                // Check if edittext is empty
                if (TextUtils.isEmpty(s)) {
                    //Disable ok button
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).isEnabled = false
                } else {
                    // Something into edit text. Enable the button.
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).isEnabled = true
                }

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && data != null) {

            if (data != null) {

            }

        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun setGravity() {
        if (pref?.language.equals("ar", ignoreCase = true)) {
            definitionName.gravity = Gravity.RIGHT
            category.gravity = Gravity.RIGHT

        } else {
            definitionName.gravity = Gravity.LEFT
            category.gravity = Gravity.LEFT
        }
    }

    companion object {

        var ACTIVITY_NAME = "controllers.applications.AssessorApplicationStagesCeriteriaDetailScreenActivity"
    }

}
