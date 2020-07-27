package com.esp.library.exceedersesp.fragments.applications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_EndlessRecyclerViewScrollListener
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.applications.ESP_LIB_ListUsersApplicationsAdapter
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.data.applicants.ESP_LIB_ResponseApplicationsDAO
import java.util.*

class ESP_LIB_UsersSearchApplicationsFragment : androidx.fragment.app.Fragment() {

    internal var context: ESP_LIB_BaseActivity? = null
    internal var mApplicationAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>? = null
    private var mApplicationLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var call: Call<ESP_LIB_ResponseApplicationsDAO>? = null
    internal var app_actual_list: MutableList<ESP_LIB_ApplicationsDAO>? = null
    internal var imm: InputMethodManager? = null

    internal var PAGE_NO = 1
    internal var PER_PAGE_RECORDS = 12
    internal var IN_LIST_RECORDS = 0
    internal var SCROLL_TO = 0
    internal var TOTAL_RECORDS_AVAILABLE = 0
    internal var SEARCH_TEXT = ""
    internal var IS_SEARCH = false

    var no_application_available_div: LinearLayout? = null
    var app_search_list: androidx.recyclerview.widget.RecyclerView? = null
    var load_more_div: RelativeLayout? = null
    var progress_search: ProgressBar? = null
    var search_result: TextView? = null
    var txtrequestcount: TextView? = null


    private fun AddScroller() {
        val toolbar = activity?.findViewById(R.id.toolbar) as Toolbar
        if (mApplicationLayoutManager != null) {
            app_search_list?.addOnScrollListener(object : ESP_LIB_EndlessRecyclerViewScrollListener(mApplicationLayoutManager as androidx.recyclerview.widget.LinearLayoutManager?) {
                override fun onHide() {
                    //  Shared.getInstance().setToolbarHeight(toolbar, false)
                }

                override fun onShow() {
                    //   Shared.getInstance().setToolbarHeight(toolbar, true)
                }

                override fun getFooterViewType(defaultNoFooterViewType: Int): Int {
                    return defaultNoFooterViewType
                }

                override fun onLoadMore(page: Int, totalItemsCount: Int) {

                    if (IN_LIST_RECORDS < TOTAL_RECORDS_AVAILABLE) {
                        LoadApplications(true, IS_SEARCH, SEARCH_TEXT)
                    }

                }

            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity as ESP_LIB_BaseActivity?

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_fragment_users_search_applications, container, false)
        initailize(v)

        return v
    }

    private fun initailize(v: View) {
        mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        app_search_list = v.findViewById(R.id.app_search_list)
        no_application_available_div = v.findViewById(R.id.no_application_available_div)
        load_more_div = v.findViewById(R.id.load_more_div)
        progress_search = v.findViewById(R.id.progress_search)
        search_result = v.findViewById(R.id.search_result)
        txtrequestcount = v.findViewById(R.id.txtrequestcount)

        app_search_list?.setHasFixedSize(true)
        app_search_list?.layoutManager = mApplicationLayoutManager
        app_search_list?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        app_search_list?.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
            }
        })


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun LoadApplications(isLoadMore: Boolean, isSearch: Boolean, searched_txt: String) {

        if (isLoadMore)
            load_more_div?.visibility = View.VISIBLE
        else
            start_loading_animation_search()
        /* APIs Mapping respective Object*/
        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

        if (!isLoadMore) {
            PAGE_NO = 1
            PER_PAGE_RECORDS = 12
            IN_LIST_RECORDS = 0
            TOTAL_RECORDS_AVAILABLE = 0
        }

        ESP_LIB_ESPApplication.getInstance().filter.pageNo = PAGE_NO
        ESP_LIB_ESPApplication.getInstance().filter.recordPerPage = PER_PAGE_RECORDS
        ESP_LIB_ESPApplication.getInstance().filter.search = searched_txt

        val list = ArrayList<String>()
        val definitionList = ArrayList<Int>()
        list.add("0")
        definitionList.add(0)

        ESP_LIB_ESPApplication.getInstance().filter.statuses = list
        ESP_LIB_ESPApplication.getInstance().filter.definitionIds = definitionList

        call = apis.getUserApplicationsV3(ESP_LIB_ESPApplication.getInstance().filter)
        call?.enqueue(object : Callback<ESP_LIB_ResponseApplicationsDAO> {
            override fun onResponse(call: Call<ESP_LIB_ResponseApplicationsDAO>, ESPLIBResponse: Response<ESP_LIB_ResponseApplicationsDAO>?) {

                if (isLoadMore)
                    load_more_div?.visibility = View.GONE
                else
                    stop_loading_animation_search()

                if (ESPLIBResponse != null && ESPLIBResponse.body() != null && ESPLIBResponse.body().totalRecords > 0) {
                    if (ESPLIBResponse.body().applications != null && ESPLIBResponse.body().applications!!.size > 0) {

                        if (isLoadMore) {
                            if (app_actual_list == null) {
                                app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?
                            } else if (app_actual_list != null && app_actual_list!!.size > 0) {
                                app_actual_list?.addAll(ESPLIBResponse.body().applications!!)
                            }

                            PAGE_NO++
                            IN_LIST_RECORDS = app_actual_list!!.size
                            TOTAL_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords
                            SCROLL_TO += PER_PAGE_RECORDS


                            mApplicationAdapter = context?.let { ESP_LIB_ListUsersApplicationsAdapter(app_actual_list, it, searched_txt, false) }
                            app_search_list?.adapter = mApplicationAdapter
                            mApplicationAdapter?.notifyDataSetChanged()
                            app_search_list?.scrollToPosition(SCROLL_TO - 5)


                        } else {

                            app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?
                            mApplicationAdapter = context?.let { ESP_LIB_ListUsersApplicationsAdapter(app_actual_list, it, searched_txt, false) }
                            app_search_list?.adapter = mApplicationAdapter
                            PAGE_NO++
                            IN_LIST_RECORDS = app_actual_list!!.size
                            TOTAL_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords
                            SCROLL_TO = 0
                            AddScroller()


                        }

                        SuccessResponse()
                        txtrequestcount?.text = TOTAL_RECORDS_AVAILABLE.toString() + " " + activity?.getString(R.string.esp_lib_text_requests)
                        if (isSearch) {
                            var result_str = ""

                            if (IN_LIST_RECORDS == 0 || IN_LIST_RECORDS == 1) {
                                result_str = getString(R.string.esp_lib_text_result)
                            } else if (IN_LIST_RECORDS > 1) {
                                result_str = getString(R.string.esp_lib_text_results)
                            }
                            search_result?.text = IN_LIST_RECORDS.toString() + " " + result_str + " " + context?.getString(R.string.esp_lib_text_found)
                        }

                    } else
                        UnSuccessResponse()
                } else
                    UnSuccessResponse()
            }

            override fun onFailure(call: Call<ESP_LIB_ResponseApplicationsDAO>, t: Throwable) {
                //Shared.getInstance().messageBox(t.getMessage().toString(),context);
                UnSuccessResponse()
            }
        })


    }//LoggedInUser end


    override fun onDestroyView() {
        if (call != null) {
            call?.cancel()
        }

        super.onDestroyView()
    }

    private fun start_loading_animation_search() {

        progress_search?.visibility = View.VISIBLE
    }

    private fun stop_loading_animation_search() {

        progress_search?.visibility = View.GONE
    }


    private fun SuccessResponse() {
        app_search_list?.visibility = View.VISIBLE
        no_application_available_div?.visibility = View.GONE
        // Shared.getInstance().hideKeyboard(activity)
    }

    private fun UnSuccessResponse() {

        if (app_search_list != null && no_application_available_div != null) {
            app_search_list?.visibility = View.GONE
            no_application_available_div?.visibility = View.VISIBLE
        }
        // Shared.getInstance().hideKeyboard(activity)
    }

    fun UpdateViewAutoSearched(searched: String?) {


        if (searched != null) {

            IS_SEARCH = true
            PAGE_NO = 1
            PER_PAGE_RECORDS = 12
            IN_LIST_RECORDS = 0
            TOTAL_RECORDS_AVAILABLE = 0
            SEARCH_TEXT = searched
            if (call != null) {
                call?.cancel()
            }

            if (searched.length > 0) {
                LoadApplications(false, IS_SEARCH, SEARCH_TEXT)
            } else {

                stop_loading_animation_search()
                /* if (app_actual_list != null) {
                     app_actual_list?.clear()
                 }
                   if (mApplicationAdapter != null) {
                       mApplicationAdapter = null
                   }

                   UnSuccessResponse()
                   stop_loading_animation_search()*/
            }
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ESP_LIB_CustomLogs.displayLogs("")
    }

    companion object {
        fun newInstance(): ESP_LIB_UsersSearchApplicationsFragment {
            val fragment = ESP_LIB_UsersSearchApplicationsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
