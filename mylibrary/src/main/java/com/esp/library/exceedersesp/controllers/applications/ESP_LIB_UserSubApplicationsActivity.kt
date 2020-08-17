package com.esp.library.exceedersesp.controllers.applications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.*
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2
import kotlinx.android.synthetic.main.esp_lib_activity_no_record.*
import kotlinx.android.synthetic.main.esp_lib_fragment_subusers_applications.*
import kotlinx.android.synthetic.main.esp_lib_fragment_users_applications.app_list
import kotlinx.android.synthetic.main.esp_lib_fragment_users_applications.load_more_div
import kotlinx.android.synthetic.main.esp_lib_fragment_users_applications.swipeRefreshLayout
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.data.applicants.ESP_LIB_ResponseApplicationsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO
import utilities.data.filters.ESP_LIB_FilterDAO
import java.util.*

class ESP_LIB_UserSubApplicationsActivity : ESP_LIB_BaseActivity() {

    internal var TAG = javaClass.simpleName

    internal var context: ESP_LIB_BaseActivity? = null
    private var mApplicationAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>? = null
    private var mApplicationLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var call: Call<ESP_LIB_ResponseApplicationsDAO>? = null
    internal var profile_call: Call<String>? = null
    internal var app_actual_list: MutableList<ESP_LIB_ApplicationsDAO>? = null
    internal var anim: ESP_LIB_ProgressBarAnimation? = null

    internal var PAGE_NO = 1
    internal var PER_PAGE_RECORDS = 12
    internal var IN_LIST_RECORDS = 0
    internal var SCROLL_TO = 0
    internal var TOTAL_RECORDS_AVAILABLE = 0
    internal var mHSListener: HideShowPlus? = null
    internal var pref: ESP_LIB_SharedPreference? = null


    interface HideShowPlus {
        fun mAction(IsShown: Boolean)
    }

    private fun AddScroller() {

        app_list?.addOnScrollListener(object : ESP_LIB_EndlessRecyclerViewScrollListener(mApplicationLayoutManager as androidx.recyclerview.widget.LinearLayoutManager?) {
            override fun onHide() {
                //  Shared.getInstance().setToolbarHeight(toolbar, false)
            }

            override fun onShow() {
                // Shared.getInstance().setToolbarHeight(toolbar, true)
            }

            override fun getFooterViewType(defaultNoFooterViewType: Int): Int {
                return defaultNoFooterViewType
            }

            override fun onLoadMore(page: Int, totalItemsCount: Int) {

                if (IN_LIST_RECORDS < TOTAL_RECORDS_AVAILABLE) {
                    LoadApplications(true)
                }

            }


        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_fragment_subusers_applications)
        initailize()


        app_list.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        try {
            mHSListener = context as HideShowPlus
        } catch (e: Exception) {

        }

        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {

            LoadApplications(false)
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }

        swipeRefreshLayout?.setOnRefreshListener {
            if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {

                PAGE_NO = 1
                PER_PAGE_RECORDS = 12
                IN_LIST_RECORDS = 0
                TOTAL_RECORDS_AVAILABLE = 0

                LoadApplications(false)
            } else {
                ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
                swipeRefreshLayout?.isRefreshing = false
            }
        }

        if (!checkPermission()) {
            requestPermission()
        }

    }

    private fun initailize() {
        context = this@ESP_LIB_UserSubApplicationsActivity
        pref = ESP_LIB_SharedPreference(context)
        mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        app_list.setHasFixedSize(true)
        app_list.layoutManager = mApplicationLayoutManager
        app_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_no) + " " + pref?.getlabels()?.application + " " + context?.getString(R.string.esp_lib_text_added)
        txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_startsubmittingapp) + " " + pref?.getlabels()?.application + " " + context?.getString(R.string.esp_lib_text_itseasy)

        setupToolbar()

    }

    private fun setupToolbar() {
        ibToolbarBack.setOnClickListener { finish() }
        toolbarheading?.text = getString(R.string.esp_lib_text_submissions)
    }


    fun LoadApplications(isLoadMore: Boolean) {

        if (isLoadMore) {
            load_more_div?.visibility = View.VISIBLE
        } else {
            start_loading_animation()
        }

        /* APIs Mapping respective Object*/
        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

        if (isLoadMore) {

        } else {
            PAGE_NO = 1
            PER_PAGE_RECORDS = 12
            IN_LIST_RECORDS = 0
            TOTAL_RECORDS_AVAILABLE = 0
        }

        ESP_LIB_ESPApplication.getInstance()?.filter?.pageNo = PAGE_NO
        ESP_LIB_ESPApplication.getInstance()?.filter?.recordPerPage = PER_PAGE_RECORDS


        var daoESPLIB: ESP_LIB_FilterDAO? = null
        if (ESP_LIB_ESPApplication.getInstance()?.filter?.statuses?.size == 4) {
            daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance()?.filter)
            daoESPLIB!!.statuses = null
            val empty_fitler = ArrayList<String>()
            empty_fitler.add("0")

        } else {
            daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance()?.filter)
        }


        val getDynamicStagesDAO = intent.getSerializableExtra("dynamicResponseDAO") as ESP_LIB_DynamicResponseDAO
        daoESPLIB.parentApplicationId = getDynamicStagesDAO.applicationId.toString()
        daoESPLIB.applicantId = "0"
        daoESPLIB.definationId = null
        daoESPLIB.search = ""
        // call = apis.GetUserSubApplicationsList("", 0, PAGE_NO, PER_PAGE_RECORDS, false, 1, "", getDynamicStagesDAO.linkDefinitionId);
        call = apis.getUserApplicationsV4(daoESPLIB)



        call!!.enqueue(object : Callback<ESP_LIB_ResponseApplicationsDAO> {
            override fun onResponse(call: Call<ESP_LIB_ResponseApplicationsDAO>, ESPLIBResponse: Response<ESP_LIB_ResponseApplicationsDAO>?) {

                if (isLoadMore) {
                    load_more_div?.visibility = View.GONE
                }

                if (ESPLIBResponse != null && ESPLIBResponse.body() != null && ESPLIBResponse.body().totalRecords > 0) {
                    if (ESPLIBResponse.body().applications != null && ESPLIBResponse.body().applications!!.size > 0) {


                        if (isLoadMore) {
                            if (app_actual_list == null) {
                                app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?
                            } else if (app_actual_list != null && app_actual_list!!.size > 0) {
                                app_actual_list!!.addAll(ESPLIBResponse.body().applications!!)
                            }

                            PAGE_NO++
                            IN_LIST_RECORDS = app_actual_list!!.size
                            TOTAL_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords
                            SCROLL_TO += PER_PAGE_RECORDS

                            try {
                                mApplicationAdapter = context?.let { ESP_LIB_ListUsersApplicationsAdapterV2(app_actual_list, it, "", true) }
                                app_list?.adapter = mApplicationAdapter
                                mApplicationAdapter!!.notifyDataSetChanged()
                                app_list?.scrollToPosition(SCROLL_TO - 5)
                            } catch (e: java.lang.Exception) {
                            }

                        } else {
                            try {
                                app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?
                                mApplicationAdapter = context?.let { ESP_LIB_ListUsersApplicationsAdapterV2(app_actual_list, it, "", true) }
                                app_list?.adapter = mApplicationAdapter
                                PAGE_NO++
                                IN_LIST_RECORDS = app_actual_list!!.size
                                TOTAL_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords
                                SCROLL_TO = 0
                                AddScroller()
                            } catch (e: java.lang.Exception) {
                            }
                        }

                        txtrequests.text = TOTAL_RECORDS_AVAILABLE.toString() + " " + getString(R.string.esp_lib_text_submissions)
                        SuccessResponse()

                        if (!isLoadMore) {
                            GetProfileStatus()
                        } else
                            stop_loading_animation()

                    } else {
                        if (!isLoadMore) {
                            GetProfileStatus()
                        } else
                            stop_loading_animation()
                        UnSuccessResponse()
                    }
                } else {
                    if (!isLoadMore) {
                        GetProfileStatus()
                    } else
                        stop_loading_animation()
                    UnSuccessResponse()
                }


            }


            override fun onFailure(call: Call<ESP_LIB_ResponseApplicationsDAO>, t: Throwable) {
                ESP_LIB_Shared.getInstance().messageBox(t.message, context)
                stop_loading_animation()
                UnSuccessResponse()
                if (!isLoadMore) {
                    GetProfileStatus()
                }

            }
        })

    }//LoggedInUser end


    override fun onDestroy() {
        super.onDestroy()
        if (call != null) {
            call!!.cancel()
        }

    }

    private fun start_loading_animation() {

        shimmerview_container.visibility = View.VISIBLE
        app_list?.visibility = View.GONE


    }

    private fun stop_loading_animation() {


        swipeRefreshLayout?.isRefreshing = false
        shimmerview_container.visibility = View.GONE
        app_list?.visibility = View.VISIBLE


    }

    private fun SuccessResponse() {
        app_list?.visibility = View.VISIBLE
        no_subapplication_available_div?.visibility = View.GONE

        if (app_actual_list != null && app_actual_list!!.size > 0) {

            if (mHSListener != null) {
                mHSListener!!.mAction(false)
            }

        }
    }

    private fun UnSuccessResponse() {
        app_list?.visibility = View.GONE
        no_subapplication_available_div?.visibility = View.VISIBLE

        if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.assessor.toString(), ignoreCase = true)) {
            add_btn?.visibility = View.VISIBLE
            detail_text?.visibility = View.VISIBLE
        } else {
            add_btn?.visibility = View.GONE
            detail_text?.visibility = View.GONE
        }

    }

    fun GetProfileStatus() {

        if (!ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.assessor.toString(), ignoreCase = true)) {
            stop_loading_animation()
            return
        }


        if(ESP_LIB_ESPApplication.getInstance().isComponent)
        {
            stop_loading_animation()
            return
        }

        start_loading_animation()

        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

        profile_call = apis.userProfileStatus
        profile_call!!.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>?) {

                stop_loading_animation()

                if (response != null && response.body() != null) {

                    val response_text = response.body()
                    ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus = response_text

                    if (response_text.equals(context?.getString(R.string.esp_lib_text_profile_complete), ignoreCase = true)) {
                    } else if (response_text.equals(context?.getString(R.string.esp_lib_text_profile_incomplete), ignoreCase = true)) {
                        ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc), context)

                    } else if (response_text.equals(context?.getString(R.string.esp_lib_text_profile_incomplete_admin), ignoreCase = true)) {
                        ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc_admin), context)
                    }

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                stop_loading_animation()
            }
        })


    }


    private fun checkPermission(): Boolean {

        val permissionInternal = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) }
        val permissionExternal = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) }

        val listPermissionsNeeded = ArrayList<String>()

        if (permissionInternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            context?.let { ActivityCompat.requestPermissions(it, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS) }
            return false
        }
        return true
    }

    private fun requestPermission() {
        context?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted) {
                    //"Permission Granted, Now you can access location data."

                    try {
                        ESP_LIB_Shared.getInstance().createFolder(ESP_LIB_Constants.FOLDER_PATH, ESP_LIB_Constants.FOLDER_NAME, context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }
        }
    }

    companion object {
        private val PERMISSION_REQUEST_CODE = 200
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    }

    override fun onBackPressed() {
        super.onBackPressed()
        ESP_LIB_ListUsersApplicationsAdapterV2.isSubApplications = false
    }

}