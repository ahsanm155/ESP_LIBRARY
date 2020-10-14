package com.esp.library.exceedersesp.fragments.applications

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationActivityTabs
import com.esp.library.exceedersesp.controllers.applications.filters.ESP_LIB_FilterScreenActivity
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.customevents.EventOptions
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.esp_lib_activity_no_record.view.*
import kotlinx.android.synthetic.main.esp_lib_activity_search_layout.view.*
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.*
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.app_list
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.ivfilter
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.llcontentlayout
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.load_more_div
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.nestedscrollview
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.rlsearchbar
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.swipeRefreshLayout
import kotlinx.android.synthetic.main.esp_lib_fragment_seeall_applications.view.txtrequestcount
import kotlinx.android.synthetic.main.esp_lib_fragment_users_applications.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.applications.ESP_LIB_ListCardsApplicationsAdapter
import utilities.common.ESP_LIB_CommonMethodsKotlin
import utilities.common.ESP_LIB_CommonMethodsKotlin.Companion.getthemeColor
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.data.applicants.ESP_LIB_ResponseApplicationsDAO
import utilities.data.applicants.addapplication.ESP_LIB_DefinationsDAO
import utilities.data.filters.ESP_LIB_FilterDAO
import java.util.*


class ESP_LIB_SeeAllApplicationsFragment : androidx.fragment.app.Fragment() {


    internal var TAG = javaClass.simpleName

    internal var context: Context? = null
    internal var mApplicationAdapterESPLIB: ESP_LIB_ListUsersApplicationsAdapterV2? = null
    private var mApplicationCardAdapter: ESP_LIB_ListCardsApplicationsAdapter? = null
    private var mApplicationLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var loadDefinitionCall: Call<ESP_LIB_ResponseApplicationsDAO>? = null
    internal var app_actual_list: MutableList<ESP_LIB_ApplicationsDAO>? = null
    internal var app_actual_card_list: ArrayList<ESP_LIB_ApplicationsDAO>? = null
    internal var imm: InputMethodManager? = null
    internal var PAGE_NO = 1
    internal var PER_PAGE_RECORDS = 12
    internal var IN_LIST_RECORDS = 0
    internal var SCROLL_TO = 0
    internal var TOTAL_RECORDS_AVAILABLE = 0

    internal var PAGE_NO_CARD = 1
    internal var PER_PAGE_CARD_RECORDS = 12
    internal var IN_LIST_CARD_RECORDS = 0
    internal var TOTAL_CARD_RECORDS_AVAILABLE = 0
    internal var mHSListener: HideShowPlus? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var shimmer_view_container: ShimmerFrameLayout? = null
    internal var shimmer_view_container_cards: ShimmerFrameLayout? = null
    internal var searchText: String = ""
    internal var ivfilter: ImageView?=null
    internal var isEventRefreshData: Boolean=false

    internal var definition_list: ArrayList<ESP_LIB_DefinationsDAO> = ArrayList()


    interface HideShowPlus {
        fun mAction(IsShown: Boolean)
    }


    private fun AddScroller() {

        view?.nestedscrollview?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (IN_LIST_RECORDS < TOTAL_RECORDS_AVAILABLE) {
                    poplateApis(true, searchText)
                }
            }
        })


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity
        pref = ESP_LIB_SharedPreference(context)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_fragment_seeall_applications, container, false)
        initailize(v)


        v.app_list.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                imm?.hideSoftInputFromWindow(view!!.windowToken, 0)
            }
        })


        ivfilter?.setOnClickListener {
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_FilterScreenActivity::class.java, requireActivity(),null, 2)
        }





        v.etxtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(search_text: CharSequence, start: Int, before: Int, count: Int) {

                val title = arguments?.getString("title")
                if (title.equals(getString(R.string.esp_lib_text_feed), ignoreCase = true)) {
                    if (search_text.isEmpty()) {
                        v.ibClear.visibility = View.GONE
                    } else {
                        v.ibClear.visibility = View.VISIBLE
                    }

                    mApplicationCardAdapter?.filter?.filter(search_text)
                    val handler = Handler()
                    handler.postDelayed({
                        view?.txtrequestcount?.text = mApplicationCardAdapter?.itemCount.toString() + " " + getString(R.string.esp_lib_text_feeds)
                    }, 100)

                } else {
                    if (search_text.isEmpty()) {
                        v.ibClear.visibility = View.GONE
                    } else
                        v.ibClear.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(search_text: Editable) {
                searchText = search_text.toString()
            }
        })

        v.ibClear.setOnClickListener {
            v.etxtsearch.setText("")
            val title = arguments?.getString("title")
            if (title.equals(getString(R.string.esp_lib_text_assigned), ignoreCase = true)) {
                isEventRefreshData = true
                poplateApis(false, "")
            }
        }


        v.etxtsearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ESP_LIB_Shared.getInstance().hideKeyboard(activity)
                    val title = arguments?.getString("title")
                    if (title.equals(getString(R.string.esp_lib_text_assigned), ignoreCase = true)) {
                        isEventRefreshData = true
                        poplateApis(false, searchText)
                    }
                    return true
                }
                return false
            }
        })

        ESP_LIB_ApplicationActivityTabs.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                var checkFilter = ESP_LIB_FilterScreenActivity.isOpenFilterApplied
                if (tab.isSelected && tab.position == 1) {
                    checkFilter = ESP_LIB_FilterScreenActivity.isCloseFilterApplied
                }
                if (checkFilter) view?.ivfilter?.setImageResource(R.drawable.esp_lib_drawable_ic_filter_green)
                else view?.ivfilter?.setImageResource(R.drawable.esp_lib_drawable_ic_filter_gray)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        return v
    }

    private fun initailize(v: View) {
        mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        v.app_list.setHasFixedSize(true)
        v.app_list.layoutManager = mApplicationLayoutManager
        v.app_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        ivfilter=v.findViewById(R.id.ivfilter)

        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
           // v.txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_no) + " " + pref?.getlabels()?.application + " " + context?.getString(R.string.esp_lib_text_added)
          //  v.txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_startsubmittingapp) + " " + pref?.getlabels()?.application + " " + context?.getString(R.string.esp_lib_text_itseasy)

        } else {
            v.txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_norecord)
        }
        shimmer_view_container = v.findViewById(R.id.shimmer_view_container)
        shimmer_view_container_cards = v.findViewById(R.id.shimmer_view_container_cards)


        val themeColor = getthemeColor(requireContext());
        v.swipeRefreshLayout?.setColorSchemeColors(themeColor, themeColor, themeColor)
    }


    private fun poplateApis(isLoadMore: Boolean, search_text: String) {
        val title = arguments?.getString("title")
        if (title != null) {
            if (title.equals(getString(R.string.esp_lib_text_feed), ignoreCase = true)) {
                getSubDefinations()
                ivfilter?.visibility=View.GONE
            }
            else
            {
                ivfilter?.visibility=View.VISIBLE
                loadApplications(isLoadMore, search_text)
            }
        }
    }

    fun loadApplications(isLoadMore: Boolean, search_text: String) {

        if(isEventRefreshData)
        {
            view?.swipeRefreshLayout?.isRefreshing = true
        }
        else if (isLoadMore) {
            view?.load_more_div?.visibility = View.VISIBLE
        } else {
            start_loading_animation()
        }


        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

        if (!isLoadMore) {
            PAGE_NO = 1
            PER_PAGE_RECORDS = 12
            IN_LIST_RECORDS = 0
            TOTAL_RECORDS_AVAILABLE = 0
        }

        if (ESP_LIB_ESPApplication.getInstance().filter == null) {
            ESP_LIB_ESPApplication.getInstance().filter = ESP_LIB_FilterDAO()
        }

        ESP_LIB_ESPApplication.getInstance().filter.pageNo = PAGE_NO
        ESP_LIB_ESPApplication.getInstance().filter.recordPerPage = PER_PAGE_RECORDS
        ESP_LIB_ESPApplication.getInstance().filter.search = search_text

        var daoESPLIB: ESP_LIB_FilterDAO? = null
        val list = ArrayList<String>()
        val title = arguments?.getString("title")
        if (title != null) {

            daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance()?.filter)
            if (title.equals(getString(R.string.esp_lib_text_assigned), ignoreCase = true)) {

                daoESPLIB.isMySpace = true
                daoESPLIB.isFilterApplied = false
                daoESPLIB.myApplications = false
                daoESPLIB.type = 3
                list.add("1")
                daoESPLIB.statuses = list
                //daoESPLIB.sortBy=4
            }
        }
        loadDefinitionCall = apis.getUserApplicationsV4(daoESPLIB!!)
        loadDefinitionCall!!.enqueue(
                object : Callback<ESP_LIB_ResponseApplicationsDAO> {
                    override fun onResponse(call: Call<ESP_LIB_ResponseApplicationsDAO>, ESPLIBResponse: Response<ESP_LIB_ResponseApplicationsDAO>?) {
                        loadDefinitionCall = null
                        if (isLoadMore) {
                            view?.load_more_div?.visibility = View.GONE
                        } else
                            app_actual_list?.clear()

                        loadDefinitionCall = null



                        if (ESPLIBResponse?.body() != null && ESPLIBResponse.body().totalRecords > 0) {
                            if (ESPLIBResponse.body().applications != null && ESPLIBResponse.body().applications!!.size > 0) {

                                if (isLoadMore) {
                                    if (app_actual_list == null) {
                                        app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?
                                    } else if (app_actual_list != null && app_actual_list!!.size > 0) {

                                        //val app_actual_list_temp = filterData(response)
                                        if (app_actual_list != null)
                                            app_actual_list?.addAll(ESPLIBResponse.body().applications!!)
                                        else
                                            app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?


                                    }



                                    PAGE_NO++
                                    IN_LIST_RECORDS = removeDuplication(app_actual_list).size
                                    TOTAL_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords
                                    SCROLL_TO += PER_PAGE_RECORDS

                                    mApplicationAdapterESPLIB = context?.let { ESP_LIB_ListUsersApplicationsAdapterV2(removeDuplication(app_actual_list), it, "", false) }
                                    view?.app_list?.adapter = mApplicationAdapterESPLIB
                                    mApplicationAdapterESPLIB!!.notifyDataSetChanged()

                                    view?.app_list?.scrollToPosition(SCROLL_TO - 3)

                                } else {

                                    // val app_actual_list_temp = filterData(response)
                                    if (app_actual_list != null)
                                        app_actual_list?.addAll(ESPLIBResponse.body().applications!!)
                                    else
                                        app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?

                                    mApplicationAdapterESPLIB = context?.let { ESP_LIB_ListUsersApplicationsAdapterV2(removeDuplication(app_actual_list), it, "", false) }
                                    view?.app_list?.adapter = mApplicationAdapterESPLIB

                                    PAGE_NO++
                                    IN_LIST_RECORDS = removeDuplication(app_actual_list).size
                                    TOTAL_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords
                                    SCROLL_TO = 0
                                    AddScroller()


                                }

                                view?.txtrequestcount?.text = TOTAL_RECORDS_AVAILABLE.toString() + " " + activity?.getString(R.string.esp_lib_text_open_requests)
                                SuccessResponse()

                                stop_loading_animation()

                            } else {

                                stop_loading_animation()
                                if (app_actual_list == null || app_actual_list?.size == 0)
                                    UnSuccessResponse()
                            }
                        } else {

                            stop_loading_animation()
                            if (app_actual_list == null || app_actual_list?.size == 0)
                                UnSuccessResponse()
                        }


                    }


                    override fun onFailure(call: Call<ESP_LIB_ResponseApplicationsDAO>, t: Throwable) {
                        //  Shared.getInstance().messageBox(t.message, context)
                        stop_loading_animation()
                        UnSuccessResponse()


                    }
                })


    }//LoggedInUser end

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dataRefreshEvent(eventRefreshData: EventOptions.EventRefreshData) {

        val tabLayout: TabLayout? = ESP_LIB_ApplicationActivityTabs.tabLayout
        val tab_position = tabLayout?.selectedTabPosition

        var checkFilter = ESP_LIB_FilterScreenActivity.isOpenFilterApplied
        if (tab_position == 1) checkFilter = ESP_LIB_FilterScreenActivity.isCloseFilterApplied

        if (checkFilter) view?.ivfilter?.setImageResource(R.drawable.esp_lib_drawable_ic_filter_green)
        else view?.ivfilter?.setImageResource(R.drawable.esp_lib_drawable_ic_filter_gray)

        isEventRefreshData = true
        reLoadApplications()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }



    fun getSubDefinations() {

        start_loading_animation()

        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
        val def_call = apis.getfeedsList()
        def_call.enqueue(object : Callback<List<ESP_LIB_ApplicationsDAO>> {
            override fun onResponse(call: Call<List<ESP_LIB_ApplicationsDAO>>, response: Response<List<ESP_LIB_ApplicationsDAO>>) {



                if (response.body() != null && response.body().isNotEmpty()) {

                    populateData(response.body())

                } else {
                   UnSuccessResponse()
                    // ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)
                }

                stop_loading_animation()
            }

            override fun onFailure(call: Call<List<ESP_LIB_ApplicationsDAO>>, t: Throwable) {
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)
                stop_loading_animation()
            }
        })

    }

    private fun populateData(body: List<ESP_LIB_ApplicationsDAO>) {
        SuccessResponse()
        app_actual_card_list?.clear()
        app_actual_card_list= body as ArrayList<ESP_LIB_ApplicationsDAO>?
        mApplicationCardAdapter = context?.let { ESP_LIB_ListCardsApplicationsAdapter(app_actual_card_list, it, "", false) }
        mApplicationCardAdapter?.setFeedButtonsVisibility(true)
        view?.app_list?.adapter = mApplicationCardAdapter
        view?.txtrequestcount?.text = app_actual_card_list?.size.toString() + " " + getString(R.string.esp_lib_text_feeds)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try {
            mHSListener = context as HideShowPlus
        } catch (e: Exception) {

        }

        view?.swipeRefreshLayout?.setOnRefreshListener {
            clearAllAdapters()
            poplateApis(false, searchText)
        }



        if (!ESP_LIB_CommonMethodsKotlin.checkPermission(requireContext())) {
            ESP_LIB_CommonMethodsKotlin.requestPermission(requireContext())
        }


    }


    private fun refreshListCall() {
        when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            true -> {
                PAGE_NO = 1
                PER_PAGE_RECORDS = 12
                IN_LIST_RECORDS = 0
                TOTAL_RECORDS_AVAILABLE = 0
                //view?.txtrequestcount?.text = ""
                view?.etxtsearch?.setText("")
                poplateApis(false, "")
            }
            false -> {
                ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
                view?.swipeRefreshLayout?.isRefreshing = false
            }
        }
    }

    fun reLoadApplications() {
        when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            true -> refreshListCall()
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }

    }

    private fun clearAllAdapters() {
        PAGE_NO = 1
        PER_PAGE_RECORDS = 12
        IN_LIST_RECORDS = 0
        TOTAL_RECORDS_AVAILABLE = 0
        app_actual_list?.clear()
        app_actual_card_list?.clear()
        mApplicationAdapterESPLIB?.notifyDataSetChanged()
        mApplicationCardAdapter?.notifyDataSetChanged()
        view?.txtrequestcount?.text = ""
        view?.etxtsearch?.setText("")
    }


    fun removeDuplication(appActualList: MutableList<ESP_LIB_ApplicationsDAO>?): ArrayList<ESP_LIB_ApplicationsDAO> {
        val listCollections = ArrayList<ESP_LIB_ApplicationsDAO>()
        for (i in 0 until appActualList!!.size) {
            val getList = appActualList.get(i);
            val isArrayHasValue = listCollections.any { x -> x.id == getList.id }
            if (!isArrayHasValue) {
                listCollections.add(getList)
            }
        }

        return listCollections
    }

    override fun onDestroyView() {
        if (loadDefinitionCall != null) {
            loadDefinitionCall!!.cancel()
        }
        super.onDestroyView()
    }


    private fun start_loading_animation() {


        view?.no_application_available_div?.visibility = View.GONE
        shimmer_view_container?.visibility = View.VISIBLE
        view?.app_list?.visibility = View.GONE

    }

    private fun stop_loading_animation() {
        isEventRefreshData=false
        view?.swipeRefreshLayout?.isRefreshing = false
        shimmer_view_container?.visibility = View.GONE
        view?.app_list?.visibility = View.VISIBLE


    }

    private fun SuccessResponse() {
        view?.llcontentlayout?.visibility = View.VISIBLE
        view?.no_application_available_div?.visibility = View.GONE
        if (mHSListener != null) {
            mHSListener?.mAction(false)
        }
    }

    private fun UnSuccessResponse() {
        view?.rlsearchbar?.visibility = View.GONE
        view?.llcontentlayout?.visibility = View.GONE
        view?.no_application_available_div?.visibility = View.VISIBLE
        try {
            view?.ivnorecordinside?.visibility = View.VISIBLE
            if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
                view?.txtnoapplicationadded?.text=getString(R.string.esp_lib_text_no_applications)
                view?.detail_text?.visibility = View.GONE
                if (mHSListener != null) {
                    mHSListener?.mAction(false)
                }

            } else {
                view?.detail_text?.visibility = View.GONE
            }
        } catch (e: java.lang.Exception) {

        }

        if (!searchText.isNullOrEmpty()) {
            view?.txtnoapplicationadded?.text = getString(R.string.esp_lib_text_norecord)
            view?.detail_text?.visibility = View.GONE
            view?.rlsearchbar?.visibility = View.VISIBLE
        }

    }


    companion object {


        fun newInstance(): ESP_LIB_SeeAllApplicationsFragment {
            val fragment = ESP_LIB_SeeAllApplicationsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(title: String): androidx.fragment.app.Fragment {
            val fragment = ESP_LIB_SeeAllApplicationsFragment()
            val args = Bundle()
            args.putString("title", title);
            fragment.arguments = args
            return fragment
        }
    }


    override fun onResume() {
        super.onResume()
        reLoadApplications()
    }

}
