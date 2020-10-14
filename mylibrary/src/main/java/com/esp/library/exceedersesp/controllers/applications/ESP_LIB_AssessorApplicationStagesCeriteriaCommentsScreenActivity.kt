package com.esp.library.exceedersesp.controllers.applications

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_AssesscorApplicationStagesCriteriaCommentsFragment
import com.esp.library.utilities.common.*
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.esp_lib_add_comments_view.*
import kotlinx.android.synthetic.main.esp_lib_gradientcurvetoolbar.*
import kotlinx.android.synthetic.main.esp_lib_repeater_application_stages_criteria_comments.user_img
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.applications.ESP_LIB_ListApplicationStageCriteriaCommentsAdapter
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.addapplication.ESP_LIB_PostApplicationsCriteriaCommentsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaCommentsListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO

class ESP_LIB_AssessorApplicationStagesCeriteriaCommentsScreenActivity : ESP_LIB_BaseActivity(), ESP_LIB_ListApplicationStageCriteriaCommentsAdapter.CriteriaStatusChange {
    internal var context: ESP_LIB_BaseActivity? = null
    internal var detail_call: Call<ESP_LIB_DynamicResponseDAO>? = null
    internal var status_call: Call<Int>? = null
    internal var anim: ESP_LIB_ProgressBarAnimation? = null
    internal var mCriteriaESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO? = null
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var submit_request: ESP_LIB_AssesscorApplicationStagesCriteriaCommentsFragment? = null
    internal var imm: InputMethodManager? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var pDialog: android.app.AlertDialog? = null
    override fun StatusChange(update: ESP_LIB_DynamicStagesCriteriaCommentsListDAO) {
        AddCriterComments(update, mCriteriaESPLIB)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ESP_LIB_ESPApplication.getInstance().applicationTheme)
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_assessor_application_stages_criteria_comments)
        initialize()
        val bnd = intent.extras
        if (bnd != null) {
            mCriteriaESPLIB = bnd.getSerializable(ESP_LIB_DynamicStagesCriteriaListDAO.BUNDLE_KEY) as ESP_LIB_DynamicStagesCriteriaListDAO
            if (mCriteriaESPLIB != null) {
                UpdateTopView()
            }
        }
        try {
            ESP_LIB_Shared.getInstance().createFolder(ESP_LIB_Constants.FOLDER_PATH, ESP_LIB_Constants.FOLDER_NAME, context)
        } catch (e: Exception) {
        }


        fm = this.context!!.getSupportFragmentManager()
        submit_request = mCriteriaESPLIB?.let { ESP_LIB_AssesscorApplicationStagesCriteriaCommentsFragment.newInstance(it) }
        val ft = fm!!.beginTransaction()
        ft.add(R.id.request_fragment, submit_request!!)
        ft.commit()

        if (user_img != null && ESP_LIB_ESPApplication.getInstance().user != null && ESP_LIB_ESPApplication.getInstance().user.loginResponse?.imageUrl != null && ESP_LIB_ESPApplication.getInstance().user.loginResponse?.imageUrl!!.length > 0) {
            Picasso.with(context)
                    .load(ESP_LIB_ESPApplication.getInstance().user.loginResponse?.imageUrl)
                    .placeholder(R.drawable.esp_lib_drawable_ic_contact_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(ESP_LIB_RoundedPicasso())
                    .resize(30, 30)
                    .into(user_img)


        }

        if (add_comments != null) {

            add_comments.setOnClickListener { AddCriterComments(null, mCriteriaESPLIB) }

        }

    }

    private fun initialize() {
        context = this@ESP_LIB_AssessorApplicationStagesCeriteriaCommentsScreenActivity
        setSupportActionBar(gradientcurvetoolbar)
        pref = ESP_LIB_SharedPreference(context!!)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        ibToolbarBack.setOnClickListener { finish() }

    }


    private fun start_loading_animation() {
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun stop_loading_animation() {
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }

    fun GetApplicationDetail() {

        start_loading_animation()
        try {

            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

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
                            submit_request!!.UpdateCriteriaCommentsList(submit_request!!.view)
                        }

                    }
                }

                override fun onFailure(call: Call<ESP_LIB_DynamicResponseDAO>, t: Throwable) {
                    stop_loading_animation();
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ex.printStackTrace()
            ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)

        }

    }

    fun AddEditComments(ESPLIBPost: ESP_LIB_PostApplicationsCriteriaCommentsDAO) {

        val view = currentFocus
        if (view != null) {
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }

        start_loading_animation()
        try {

            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

            val UserComments = RequestBody.create(MediaType.parse("text/plain"), ESPLIBPost.comments)

            if (ESPLIBPost.id > 0) {
                status_call = apis.EditComments(ESPLIBPost.id, ESPLIBPost.assessmentId, UserComments)
            } else {
                status_call = apis.addComments(ESPLIBPost.assessmentId, UserComments)
            }



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
            if (ex != null) {
                stop_loading_animation()
                if (ex != null && context != null) {
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            }
        }

    }

    private fun UpdateTopView() {
        headingtext.text = getString(R.string.esp_lib_text_criteria_feedback)
        subheadingtext.visibility = View.VISIBLE
        subheadingtext.text = mCriteriaESPLIB?.name
    }

    fun AddCriterComments(post: ESP_LIB_DynamicStagesCriteriaCommentsListDAO?, criteriaESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?) {

        val builder = AlertDialog.Builder(context!!)

        val view = LayoutInflater.from(context).inflate(R.layout.esp_lib_dialog_layout, null)
        val input = view.findViewById<EditText>(R.id.reason)
        input.setHint(R.string.esp_lib_text_please_add_comment)
        builder.setView(view)

        if (post != null) {
            builder.setTitle(R.string.esp_lib_text_edit_comment)

            if (post.comment != null && post.comment!!.length > 0) {
                input.setText(post.comment)
            }

        } else {
            builder.setTitle(R.string.esp_lib_text_add_comment)
        }



        builder.setPositiveButton(R.string.esp_lib_text_save) { dialog, which ->
            val m_Text = input.text.toString()

            if (m_Text != null && m_Text.length > 0) {

                val post_comments = ESP_LIB_PostApplicationsCriteriaCommentsDAO()
                post_comments.assessmentId = criteriaESPLIB!!.assessmentId
                post_comments.comments = m_Text

                if (post != null) {
                    post_comments.id = post.id
                }

                imm!!.hideSoftInputFromWindow(input.windowToken, 0)

                AddEditComments(post_comments)

                dialog.cancel()


            }

            imm!!.hideSoftInputFromWindow(input.windowToken, 0)
            dialog.cancel()
        }



        builder.setNegativeButton(R.string.esp_lib_text_cancel) { dialog, which ->
            imm!!.hideSoftInputFromWindow(input.windowToken, 0)
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context!!.getResources().getColor(R.color.esp_lib_color_black))


        if (post != null && post.comment != null && post.comment!!.length > 0) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        } else {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        }


        input.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                       count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                // Check if edittext is empty
                dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE).isEnabled = !TextUtils.isEmpty(s)

            }
        })
    }


    companion object {

        var ACTIVITY_NAME = "controllers.applications.AssessorApplicationStagesCeriteriaCommentsScreenActivity"
    }

}
