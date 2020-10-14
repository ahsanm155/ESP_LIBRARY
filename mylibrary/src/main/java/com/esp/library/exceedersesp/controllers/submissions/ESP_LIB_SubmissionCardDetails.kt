package com.esp.library.exceedersesp.controllers.submissions

import android.os.Build
import android.os.Bundle
import android.view.View
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import kotlinx.android.synthetic.main.esp_lib_link_definition_card.*
import utilities.data.applicants.ESP_LIB_LinkApplicationsDAO


internal class ESP_LIB_SubmissionCardDetails : ESP_LIB_BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ESP_LIB_ESPApplication.getInstance().applicationTheme)
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_submissions_card_details)
        initailize()


        val linkApplicationsDAO = intent.getSerializableExtra("linkApplicationsDAO") as ESP_LIB_LinkApplicationsDAO
        val submissioncount: Int = linkApplicationsDAO.pendingLinkApplications + linkApplicationsDAO.acceptedLinkApplications + linkApplicationsDAO.rejectedLinkApplications

        txtallvalue.text = submissioncount.toString()
        txtpendingvalue.text = linkApplicationsDAO.pendingLinkApplications.toString()
        txtacceptedvalue.text = linkApplicationsDAO.acceptedLinkApplications.toString()
        txtrejectedvalue.text = linkApplicationsDAO.rejectedLinkApplications.toString()
        viewdivider.visibility = View.GONE
        llsubmissiondetail.setPadding(63, 0, 0, 50)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            submissionallowedtext.setTextAppearance(R.style.Esp_Lib_Style_TextHeading1Black)
        else
            submissionallowedtext.setTextAppearance(this, R.style.Esp_Lib_Style_TextHeading1Black)
        val arg = Bundle()
        val fm = getSupportFragmentManager()
        val submit_request_tabs = ESP_LIB_SubmissionActivityTabs.newInstance()
        arg.putString("actualResponseJson", intent.getStringExtra("actualResponseJson"))
        submit_request_tabs.arguments = arg
        val ft = fm.beginTransaction()
        ft.add(R.id.request_fragment, submit_request_tabs)
        ft.commit()

    }

    private fun initailize() {
        setSupportActionBar(gradienttoolbar)
        ibToolbarBack.setOnClickListener { onBackPressed() }
        toolbarheading.text = getString(R.string.esp_lib_text_submissions)

    }


    companion object {

        fun newInstance(): ESP_LIB_SubmissionCardDetails {

            return ESP_LIB_SubmissionCardDetails()
        }
    }
}


