package com.esp.library.exceedersesp.controllers.submissions

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.applications.ESP_LIB_ListSubmissionApplicationsAdapter
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.data.applicants.ESP_LIB_ResponseApplicationsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO
import utilities.data.filters.ESP_LIB_FilterDAO
import java.util.*


class ESP_LIB_SubmissionCardFragment : androidx.fragment.app.Fragment()
{

    var pDialog: AlertDialog? = null
    var app_submission_list = ArrayList<ESP_LIB_ApplicationsDAO>()
    var PAGE_NO = 1
    var PER_PAGE_RECORDS = 12
    var IN_LIST_RECORDS = 0
    var SCROLL_TO = 0
    var TOTAL_RECORDS_AVAILABLE = 0
    var app_list: RecyclerView? = null
    var submissionApplicationsAdapter:ESP_LIB_ListSubmissionApplicationsAdapter?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_activity_submission_card_fragment, container, false)
        initalize(v)
        getSubmissions(false)


        return v
    }

    private fun initalize(v: View?) {


        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(requireContext())
        val mApplicationSubmissionLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        app_list = v?.findViewById(R.id.app_list)
        //app_list?.setHasFixedSize(true)
        app_list?.layoutManager = mApplicationSubmissionLayoutManager
        app_list?.itemAnimator = DefaultItemAnimator()
        submissionApplicationsAdapter = ESP_LIB_ListSubmissionApplicationsAdapter(app_submission_list, requireActivity(), "",true)
        app_list?.adapter = submissionApplicationsAdapter
       // app_list?.setNestedScrollingEnabled(false);

    }



    private fun getSubmissions(isLoadMore: Boolean) {
        start_loading_animation()
        val list = ArrayList<String>()
        val daoESPLIB = ESP_LIB_FilterDAO()
        val title = arguments?.getString("title")
        val actualResponseJson = arguments?.getString("actualResponseJson")

        if (title != null) {
            if(title.equals(getString(R.string.esp_lib_text_all),ignoreCase = true))
                list.add("0")
            else if(title.equals(getString(R.string.esp_lib_text_opencaps),ignoreCase = true))
                list.add("2")
            else if(title.equals(getString(R.string.esp_lib_text_accepted),ignoreCase = true))
                list.add("3")
            else if(title.equals(getString(R.string.esp_lib_text_rejected),ignoreCase = true))
                list.add("4")
        }

        try {
                PAGE_NO = 1
                PER_PAGE_RECORDS = 12
                IN_LIST_RECORDS = 0
                TOTAL_RECORDS_AVAILABLE = 0
            ESP_LIB_ESPApplication.getInstance().filter.pageNo = PAGE_NO
            ESP_LIB_ESPApplication.getInstance().filter.recordPerPage = PER_PAGE_RECORDS
            daoESPLIB.statuses=list
            val getDynamicStagesDAO = Gson().fromJson<ESP_LIB_DynamicResponseDAO>(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)

            daoESPLIB.parentApplicationId = getDynamicStagesDAO.applicationId.toString()
            daoESPLIB.applicantId = "0"
            daoESPLIB.definationId = null
            daoESPLIB.search = ""
            daoESPLIB.type = 1
            val apis = CompRoot().getService(context)
            val call = apis.getUserApplicationsV4(daoESPLIB)
            call.enqueue(object : Callback<ESP_LIB_ResponseApplicationsDAO?> {
                override fun onResponse(call: Call<ESP_LIB_ResponseApplicationsDAO?>, response: Response<ESP_LIB_ResponseApplicationsDAO?>) {
                    stop_loading_animation()
                    if (response != null && response.body() != null && response.body()!!.totalRecords > 0) {
                        if (response.body()!!.applications != null && response.body()!!.applications!!.isNotEmpty()) {
                            if (isLoadMore) {
                                if (app_submission_list == null) {
                                    response.body()!!.applications?.let { app_submission_list.addAll(it) }
                                } else if (app_submission_list != null && app_submission_list.size > 0) {
                                    response.body()!!.applications?.let { app_submission_list.addAll(it) }
                                }
                                PAGE_NO++
                                IN_LIST_RECORDS = app_submission_list.size
                                TOTAL_RECORDS_AVAILABLE = response.body()!!.totalRecords
                                SCROLL_TO += PER_PAGE_RECORDS
                                    submissionApplicationsAdapter?.notifyDataSetChanged()
                            } else {
                                try {
                                    app_submission_list.clear()
                                    response.body()!!.applications?.let { app_submission_list.addAll(it) }
                                    /*app_submission_list.addAll(app_submission_list)
                                    app_submission_list.addAll(app_submission_list)
                                    app_submission_list.addAll(app_submission_list)
                                    app_submission_list.addAll(app_submission_list)*/
                                    submissionApplicationsAdapter?.notifyDataSetChanged()
                                    PAGE_NO++
                                    IN_LIST_RECORDS = app_submission_list.size
                                    TOTAL_RECORDS_AVAILABLE = response.body()!!.totalRecords
                                    SCROLL_TO = 0
                                   // AddScroller()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), requireActivity())
                    }
                 //   txtsubmissions.setText(getString(R.string.esp_lib_text_submissions) + " (" + app_submission_list.size + ")")
                }

                override fun onFailure(call: Call<ESP_LIB_ResponseApplicationsDAO?>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), requireActivity())
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), requireActivity())
        }
    }

    private fun start_loading_animation() {

            try {
                if (!pDialog!!.isShowing()) pDialog?.show()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
    }

    private fun stop_loading_animation() {

            try {
                if (pDialog!!.isShowing()) pDialog?.dismiss()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

    }


    companion object {

        fun newInstance(): ESP_LIB_SubmissionCardFragment {
            val fragment = ESP_LIB_SubmissionCardFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }

        fun newInstance(title: String,actualResponseJson:String): androidx.fragment.app.Fragment {
            val fragment = ESP_LIB_SubmissionCardFragment()
            val args = Bundle()
            args.putString("title", title);
            args.putString("actualResponseJson", actualResponseJson);
            fragment.arguments = args
            return fragment
        }
    }
}