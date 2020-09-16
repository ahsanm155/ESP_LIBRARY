package com.esp.library.exceedersesp.fragments.applications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_AddApplicationsActivity
import com.esp.library.exceedersesp.controllers.applications.filters.ESP_LIB_FilterScreenActivity
import com.esp.library.exceedersesp.controllers.feedback.ESP_LIB_FeedbackForm
import com.esp.library.exceedersesp.controllers.tindercard.*
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.customcontrols.ESP_LIB_BodyText
import com.esp.library.utilities.customevents.EventOptions
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.esp_lib_activity_no_record.view.*
import kotlinx.android.synthetic.main.esp_lib_activity_search_layout.view.*
import kotlinx.android.synthetic.main.esp_lib_fragment_users_applications.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.applications.ESP_LIB_ListCardsApplicationsAdapter
import utilities.common.ESP_LIB_CommonMethodsKotlin
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.data.applicants.ESP_LIB_FirebaseTokenDAO
import utilities.data.applicants.ESP_LIB_ResponseApplicationsDAO
import utilities.data.applicants.addapplication.ESP_LIB_CategoriesDAO
import utilities.data.applicants.addapplication.ESP_LIB_DefinationsDAO
import utilities.data.filters.ESP_LIB_FilterDAO
import utilities.interfaces.ESP_LIB_AnyClick
import utilities.interfaces.ESP_LIB_DeleteDraftListener
import java.util.*


class ESP_LIB_UsersApplicationsFragment : androidx.fragment.app.Fragment(), CardStackListener, ESP_LIB_DeleteDraftListener,
        ESP_LIB_AnyClick {


    internal var TAG = javaClass.simpleName
    internal var context: Context? = null
    internal var mApplicationAdapterESPLIB: ESP_LIB_ListUsersApplicationsAdapterV2? = null
    private var mApplicationCardAdapter: ESP_LIB_ListCardsApplicationsAdapter? = null
    private var mApplicationLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var loadDefinitionCall: Call<ESP_LIB_ResponseApplicationsDAO>? = null
    internal var callCards: Call<ESP_LIB_ResponseApplicationsDAO>? = null
    internal var profile_call: Call<String>? = null
    internal var app_actual_list: MutableList<ESP_LIB_ApplicationsDAO>? = null
    internal var app_actual_card_list: ArrayList<ESP_LIB_ApplicationsDAO>? = null
    internal var imm: InputMethodManager? = null
    internal var cardStackView: CardStackView? = null
    internal var rlcardstack: RelativeLayout? = null
    internal var cardManager: CardStackLayoutManager? = null
    internal var cardAdapterESPLIB: ESP_LIB_ListCardsApplicationsAdapter? = null
    internal var PAGE_NO = 1
    internal var PER_PAGE_RECORDS = 12
    internal var IN_LIST_RECORDS = 0
    internal var SCROLL_TO = 0
    internal var TOTAL_RECORDS_AVAILABLE = 0
    internal var pDialog: android.app.AlertDialog? = null

    internal var PAGE_NO_CARD = 1
    internal var PER_PAGE_CARD_RECORDS = 12
    internal var IN_LIST_CARD_RECORDS = 0
    internal var TOTAL_CARD_RECORDS_AVAILABLE = 0
    var isEventRefreshData: Boolean = false
    internal var mHSListener: HideShowPlus? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var shimmer_view_container: ShimmerFrameLayout? = null
    internal var shimmer_view_container_cards: ShimmerFrameLayout? = null
    internal var searchText: String = ""

    interface HideShowPlus {
        fun mAction(IsShown: Boolean)
    }


    private fun AddScroller() {

        view?.nestedscrollview?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (IN_LIST_RECORDS < TOTAL_RECORDS_AVAILABLE) {
                    isEventRefreshData = false
                    loadApplications(true, searchText)
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
        val v = inflater.inflate(R.layout.esp_lib_fragment_users_applications, container, false)
        initailize(v)
        initializeCards()
        //  setGravity(v)


        v.app_list.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                imm?.hideSoftInputFromWindow(view!!.windowToken, 0)
            }
        })


        v.txtseeall.setOnClickListener {

            val intent = Intent(activity, ESP_LIB_UsersCardApplications::class.java);
            intent.putExtra("searchText", searchText)
            startActivity(intent)

        }

        // v.add_btn.text = context?.getString(R.string.esp_lib_text_submit) + " " + pref?.getlabels()?.application
        v.add_btn.text = context?.getString(R.string.esp_lib_text_explore_services)
        v.add_btn.setOnClickListener { view ->
            if (ESP_LIB_ESPApplication.getInstance().user.profileStatus == null || ESP_LIB_ESPApplication.getInstance().user.profileStatus.equals(context?.getString(R.string.esp_lib_text_profile_complete), ignoreCase = true)) {
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AddApplicationsActivity::class.java, context as Activity?, null, 2)
            } else if (ESP_LIB_ESPApplication.getInstance().user.profileStatus == context?.getString(R.string.esp_lib_text_profile_incomplete)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc), context)

            } else if (ESP_LIB_ESPApplication.getInstance().user.profileStatus == getString(R.string.esp_lib_text_profile_incomplete_admin)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc_admin), context)
            }
        }

        reLoadApplications()

        when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            true -> {
                postFirebaseToken()
            }
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }

        v.ivfilter?.setOnClickListener {
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_FilterScreenActivity::class.java, requireActivity(), null, 2)
        }

        /*when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            true -> postFirebaseToken()
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }*/

        v.etxtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(search_text: CharSequence, start: Int, before: Int, count: Int) {
                if (search_text.isEmpty()) {
                    isEventRefreshData = true
                    v.ibClear.visibility = View.GONE
                } else
                    v.ibClear.visibility = View.VISIBLE
            }

            override fun afterTextChanged(search_text: Editable) {
                searchText = search_text.toString()
            }
        })

        v.ibClear.setOnClickListener {
            v.etxtsearch.setText("")
            isEventRefreshData = true
            loadApplications(false, "")
        }


        v.etxtsearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ESP_LIB_Shared.getInstance().hideKeyboard(activity)
                    //view?.txtrequestcount?.text = ""
                    isEventRefreshData = true
                    loadApplications(false, searchText)
                    return true
                }
                return false
            }
        })


        return v
    }

    private fun initailize(v: View) {
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        mApplicationLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        v.app_list.setHasFixedSize(true)
        v.app_list.layoutManager = mApplicationLayoutManager
        v.app_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()


        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
            //   v.txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_no) + " " + pref?.getlabels()?.application + " " + context?.getString(R.string.esp_lib_text_added)
            //  v.txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_startsubmittingapp) + " " + pref?.getlabels()?.application + " " + context?.getString(R.string.esp_lib_text_itseasy)

            v.txtnoapplicationadded?.text = getString(R.string.esp_lib_text_access_features)
        } else {
            v.txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_norecord)
        }




        shimmer_view_container = v.findViewById(R.id.shimmer_view_container)
        shimmer_view_container_cards = v.findViewById(R.id.shimmer_view_container_cards)
        cardStackView = v.findViewById(R.id.card_stack_view)
        rlcardstack = v.findViewById(R.id.rlcardstack)
        cardManager = CardStackLayoutManager(activity, this)

        val themeColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        v.swipeRefreshLayout?.setColorSchemeColors(themeColor, themeColor, themeColor)



        setCardAdapter(app_actual_card_list)
    }

    private fun setCardAdapter(appActualCardList: ArrayList<ESP_LIB_ApplicationsDAO>?) {
        mApplicationCardAdapter = context?.let { ESP_LIB_ListCardsApplicationsAdapter(app_actual_card_list, it, "", false) }
        mApplicationCardAdapter?.setInterfaceClickListener(this)
        cardAdapterESPLIB = this@ESP_LIB_UsersApplicationsFragment.mApplicationCardAdapter as ESP_LIB_ListCardsApplicationsAdapter?
        cardStackView?.adapter = cardAdapterESPLIB
    }

    private fun initializeCards() {
        cardManager?.setStackFrom(StackFrom.Bottom)
        cardManager?.setVisibleCount(3)
        cardManager?.setTranslationInterval(8.0f)
        cardManager?.setScaleInterval(0.95f)
        cardManager?.setSwipeThreshold(0.3f)
        cardManager?.setMaxDegree(0.0f)
        cardManager?.setDirections(Direction.HORIZONTAL)
        cardManager?.setCanScrollHorizontal(true)
        cardManager?.setCanScrollVertical(false)
        cardManager?.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        cardManager?.setOverlayInterpolator(LinearInterpolator())
        cardStackView?.layoutManager = cardManager


    }


    private fun setGravity(v: View) {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if (pref!!.language.equals("ar", ignoreCase = true)) {
            params.height = ESP_LIB_Shared.getInstance().convertDpToPixel(285F, context).toInt()

        } else {
            params.height = ESP_LIB_Shared.getInstance().convertDpToPixel(260F, context).toInt()
        }
        rlcardstack?.layoutParams = params
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")

    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${cardManager?.topPosition}, d = $direction")
        if (cardManager?.topPosition == cardAdapterESPLIB?.itemCount) {
            paginate()
        }


    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${cardManager?.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${cardManager?.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        Log.d("CardStackView", "onCardAppeared")
    }

    override fun onCardDisappeared(view: View, position: Int) {

        Log.d("CardStackView", "onCardDisappeared")
    }

    private fun paginate() {
        if (IN_LIST_CARD_RECORDS < TOTAL_CARD_RECORDS_AVAILABLE) {
            loadCardApplications(true, searchText)
        } else {
            cardAdapterESPLIB?.setSpots(cardAdapterESPLIB?.getSpots()!!)
            cardAdapterESPLIB?.notifyDataSetChanged()
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try {
            mHSListener = context as HideShowPlus
        } catch (e: Exception) {

        }



        view?.swipeRefreshLayout?.setOnRefreshListener {
            clearAllAdapters()
            loadApplications(false, searchText)
            start_loading_animation_cards()
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
                loadApplications(false, "")

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
        isEventRefreshData = false
    }

    fun loadApplications(isLoadMore: Boolean, search_text: String) {

        if (isEventRefreshData) {
            view?.swipeRefreshLayout?.isRefreshing = true
        } else {

            if (isLoadMore || (app_actual_list != null && app_actual_list!!.size > 0)) {
                view?.load_more_div?.visibility = View.VISIBLE
            } else {
                start_loading_animation()
                /*if (ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == Enums.applicant.toString())
                refreshSubDefinitionListener?.refreshSubDefinition()*/
            }
        }


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

            daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance().filter)

            daoESPLIB.isMySpace = false
            daoESPLIB.myApplications = true

            if (title.equals(getString(R.string.esp_lib_text_open), ignoreCase = true)) {
                list.add("1")
                list.add("2")

                daoESPLIB.type = 1

                daoESPLIB.statuses = list
                if (!isLoadMore)
                    loadCardApplications(false, search_text)
            } else {

                daoESPLIB.type = 2
                daoESPLIB.sortBy = 1
                if (ESP_LIB_ESPApplication.getInstance()?.filter?.statuses != null && ESP_LIB_ESPApplication.getInstance()?.filter?.statuses!!.size < 5) {
                    if (!daoESPLIB.statuses.isNullOrEmpty())
                        daoESPLIB.statuses = ArrayList<String>()
                    val tempList = ArrayList<String>()

                    for (i in ESP_LIB_ESPApplication.getInstance()?.filter?.statuses!!.indices) {
                        val get = ESP_LIB_ESPApplication.getInstance()?.filter?.statuses?.get(i)
                        if (get == "1" || get == "2") {
                        } else {
                            tempList.add(get!!)
                        }
                    }

                    daoESPLIB.statuses = tempList
                } else {
                    list.add("3")
                    list.add("4")
                    list.add("5")
                    daoESPLIB.statuses = list
                }

                rlcardstack?.visibility = View.GONE

            }

        } else {

            if (ESP_LIB_ESPApplication.getInstance()?.filter?.statuses!!.size == 4) {
                daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance()?.filter)
                daoESPLIB!!.statuses = null
                val empty_fitler = ArrayList<String>()
                empty_fitler.add("0")
                daoESPLIB.statuses = empty_fitler
            } else {
                daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance()?.filter)
            }
        }
        val apis = CompRoot().getService(context);
        loadDefinitionCall = apis?.getUserApplicationsV4(daoESPLIB)



        loadDefinitionCall?.enqueue(
                object : Callback<ESP_LIB_ResponseApplicationsDAO> {
                    override fun onResponse(call: Call<ESP_LIB_ResponseApplicationsDAO>, ESPLIBResponse: Response<ESP_LIB_ResponseApplicationsDAO>?) {
                        loadDefinitionCall = null
                        if (isLoadMore) {
                            view?.load_more_div?.visibility = View.GONE
                        } else {
                            app_actual_list?.clear()
                            setOpenRequestsAdapter()
                            view?.txtrequestcount?.text = ""
                        }

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

                                    setOpenRequestsAdapter()

                                    view?.app_list?.scrollToPosition(SCROLL_TO - 3)


                                } else {

                                    // val app_actual_list_temp = filterData(response)
                                    if (app_actual_list != null)
                                        app_actual_list?.addAll(ESPLIBResponse.body().applications!!)
                                    else
                                        app_actual_list = ESPLIBResponse.body().applications as MutableList<ESP_LIB_ApplicationsDAO>?

                                    setOpenRequestsAdapter()

                                    PAGE_NO++
                                    IN_LIST_RECORDS = removeDuplication(app_actual_list).size
                                    TOTAL_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords
                                    SCROLL_TO = 0
                                    AddScroller()


                                }
                                if (title.equals(getString(R.string.esp_lib_text_open), ignoreCase = true))
                                    view?.txtrequestcount?.text = TOTAL_RECORDS_AVAILABLE.toString() + " " + activity?.getString(R.string.esp_lib_text_open_requests)
                                else
                                    view?.txtrequestcount?.text = TOTAL_RECORDS_AVAILABLE.toString() + " " + activity?.getString(R.string.esp_lib_text_closed_requests)
                                SuccessResponse()
                                stop_loading_animation()
                                if (!isLoadMore) {
                                    GetProfileStatus()
                                }

                            } else {
                                stop_loading_animation()
                                if (!isLoadMore) {
                                    GetProfileStatus()
                                } /*else
                                    stop_loading_animation()*/
                                if (app_actual_list == null || app_actual_list?.size == 0)
                                    UnSuccessResponse()
                            }
                        } else {
                            stop_loading_animation()
                            if (!isLoadMore) {
                                GetProfileStatus()
                            } /*else
                                stop_loading_animation()*/
                            if (app_actual_list == null || app_actual_list?.size == 0)
                                UnSuccessResponse()
                        }


                    }


                    override fun onFailure(call: Call<ESP_LIB_ResponseApplicationsDAO>, t: Throwable) {
                        //  Shared.getInstance().messageBox(t.message, context)
                        stop_loading_animation()
                        UnSuccessResponse()
                        if (!isLoadMore) {
                            GetProfileStatus()
                        }

                    }
                })


    }//LoggedInUser end


    private fun setOpenRequestsAdapter() {
        if (app_actual_list != null) {
            mApplicationAdapterESPLIB = context?.let { ESP_LIB_ListUsersApplicationsAdapterV2(removeDuplication(app_actual_list), it, "", false) }
            (mApplicationAdapterESPLIB as ESP_LIB_ListUsersApplicationsAdapterV2?)?.getFragmentContext(this@ESP_LIB_UsersApplicationsFragment)
            view?.app_list?.adapter = mApplicationAdapterESPLIB
        }
    }

    fun loadCardApplications(isLoadMore: Boolean, searchText: String) {
        if (isEventRefreshData) {
            view?.swipeRefreshLayout?.isRefreshing = true
        } else {
            if (isLoadMore || (app_actual_card_list != null && app_actual_card_list!!.size > 0)) {
                if (shimmer_view_container_cards?.visibility == View.GONE)
                    view?.txtloadmorecards?.visibility = View.VISIBLE
            } else {
                start_loading_animation_cards()

            }
        }



        if (isLoadMore) {

        } else {
            PAGE_NO_CARD = 1
            PER_PAGE_CARD_RECORDS = 12
            IN_LIST_CARD_RECORDS = 0
            TOTAL_CARD_RECORDS_AVAILABLE = 0
        }

        ESP_LIB_ESPApplication.getInstance()?.filter?.pageNo = PAGE_NO_CARD
        ESP_LIB_ESPApplication.getInstance()?.filter?.recordPerPage = PER_PAGE_CARD_RECORDS
        ESP_LIB_ESPApplication.getInstance()?.filter?.search = searchText

        var daoESPLIB: ESP_LIB_FilterDAO? = null
        val list = ArrayList<String>()
        val title = arguments?.getString("title")
        if (title != null) {

            daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance()?.filter)
            if (title.equals(context?.getString(R.string.esp_lib_text_open), ignoreCase = true) ||
                    title.equals(context?.getString(R.string.esp_lib_text_pending), ignoreCase = true)) {

                daoESPLIB.isMySpace = true
                daoESPLIB.isFilterApplied = true
                daoESPLIB.myApplications = false
                list.add("2")
                daoESPLIB.statuses = list
            }

        }

        val apis = CompRoot().getService(context);
        callCards = apis?.getUserAssigned(daoESPLIB!!)

        callCards?.enqueue(
                object : Callback<ESP_LIB_ResponseApplicationsDAO> {
                    override fun onResponse(call: Call<ESP_LIB_ResponseApplicationsDAO>, ESPLIBResponse: Response<ESP_LIB_ResponseApplicationsDAO>?) {

                        callCards = null

                        if (isLoadMore) {
                            view?.txtloadmorecards?.visibility = View.GONE
                        } else
                            app_actual_card_list?.clear()

                        if (ESPLIBResponse?.body() != null && ESPLIBResponse.body().totalRecords > 0) {
                            if (ESPLIBResponse.body().applications != null && ESPLIBResponse.body().applications!!.size > 0) {

                                if (isLoadMore) {
                                    if (app_actual_card_list == null) {
                                        app_actual_card_list = ESPLIBResponse.body().applications as ArrayList<ESP_LIB_ApplicationsDAO>?
                                    } else if (app_actual_card_list != null && app_actual_card_list!!.size > 0) {

                                        if (app_actual_card_list != null)
                                            app_actual_card_list?.addAll(ESPLIBResponse.body().applications!!)
                                        else
                                            app_actual_card_list = ESPLIBResponse.body().applications as ArrayList<ESP_LIB_ApplicationsDAO>?


                                    }



                                    PAGE_NO_CARD++
                                    IN_LIST_CARD_RECORDS = removeDuplication(app_actual_card_list).size
                                    TOTAL_CARD_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords

                                    setCardAdapter(app_actual_card_list)
                                    // paginate()
                                } else {
                                    if (app_actual_card_list != null)
                                        app_actual_card_list?.addAll(ESPLIBResponse.body().applications!!)
                                    else
                                        app_actual_card_list = ESPLIBResponse.body().applications as ArrayList<ESP_LIB_ApplicationsDAO>?


                                    PAGE_NO_CARD++
                                    IN_LIST_CARD_RECORDS = removeDuplication(app_actual_card_list).size
                                    TOTAL_CARD_RECORDS_AVAILABLE = ESPLIBResponse.body().totalRecords


                                    removeDuplication(app_actual_card_list)
                                    setCardAdapter(app_actual_card_list)

                                    // paginate()

                                }

                                view?.txtseeall?.text = activity?.getString(R.string.esp_lib_text_seeall) + " (" + TOTAL_CARD_RECORDS_AVAILABLE.toString() + ")"

                                rlcardstack?.visibility = View.VISIBLE
                                stop_loading_animation_cards()

                            } else {
                                stop_loading_animation_cards()
                                rlcardstack?.visibility = View.GONE
                                UnSuccessResponse()
                            }
                        } else {
                            stop_loading_animation_cards()
                            rlcardstack?.visibility = View.GONE
                            UnSuccessResponse()
                        }


                    }


                    override fun onFailure(call: Call<ESP_LIB_ResponseApplicationsDAO>, t: Throwable) {
                        try {
                            ESP_LIB_Shared.getInstance().messageBox(activity?.getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)
                        } catch (e: java.lang.Exception) {
                        }
                        stop_loading_animation_cards()
                        view?.card_stack_view?.visibility = View.GONE

                    }
                })


    }//LoggedInUser end

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

        if (callCards != null) {
            callCards!!.cancel()
        }
        super.onDestroyView()
    }

    private fun start_loading_animation_cards() {

        if (isEventRefreshData)
            return

        view?.txtseeall?.text = ""
        view?.txtrequesttoscreen?.text = ""
        shimmer_view_container_cards?.visibility = View.VISIBLE
        view?.card_stack_view?.visibility = View.GONE

    }

    private fun stop_loading_animation_cards() {

        view?.swipeRefreshLayout?.isRefreshing = false
        view?.txtrequesttoscreen?.text = getString(R.string.esp_lib_text_requesttoaction)
        shimmer_view_container_cards?.visibility = View.GONE
        view?.card_stack_view?.visibility = View.VISIBLE


    }

    private fun start_loading_animation() {

        if (isEventRefreshData)
            return

        view?.no_application_available_div?.visibility = View.GONE
        shimmer_view_container?.visibility = View.VISIBLE
        view?.app_list?.visibility = View.GONE

    }

    private fun stop_loading_animation() {

        view?.swipeRefreshLayout?.isRefreshing = false
        shimmer_view_container?.visibility = View.GONE
        view?.app_list?.visibility = View.VISIBLE


    }

    private fun SuccessResponse() {
        view?.rlsearchbar?.visibility = View.VISIBLE
        view?.llcontentlayout?.visibility = View.VISIBLE
        view?.no_application_available_div?.visibility = View.GONE
        view?.no_record_view?.visibility = View.GONE
        if (mHSListener != null) {
            mHSListener?.mAction(false)
        }
    }

    private fun UnSuccessResponse() {

        /* if(rlcardstack?.visibility==View.VISIBLE)
             return*/




        view?.no_application_available_div?.visibility = View.VISIBLE
        view?.no_record_view?.visibility = View.VISIBLE
        if (arguments?.getString("title").equals(getString(R.string.esp_lib_text_closed), ignoreCase = true)) {
            view?.txtnoapplicationadded?.text = context?.getString(R.string.esp_lib_text_no_applications)
            view?.ivnorecordinside?.visibility = View.VISIBLE
            view?.no_record_view?.setPadding(0, 300, 0, 0)
        } else {
            val textView = view?.detail_text

            if (rlcardstack?.visibility == View.GONE && app_actual_list?.size == 0) {
                view?.rlsearchbar?.visibility = View.GONE
                view?.no_record_view?.setPadding(0, 300, 0, 0)
                //view?.llcontentlayout?.visibility = View.GONE
                view?.ivnorecordoutside?.visibility = View.GONE
                view?.ivnorecordinside?.visibility = View.VISIBLE
                TextViewCompat.setTextAppearance(view?.txtnoapplicationadded!!, R.style.Esp_Lib_Style_TextHeading5Gray);
                TextViewCompat.setTextAppearance(view?.detail_text!!, R.style.Esp_Lib_Style_TextHeading5Gray);
            } else {

                view?.ivnorecordoutside?.visibility = View.VISIBLE
                view?.ivnorecordinside?.visibility = View.GONE
                TextViewCompat.setTextAppearance(view?.txtnoapplicationadded!!, R.style.Esp_Lib_Style_TextHeading6Gray);
                TextViewCompat.setTextAppearance(view?.detail_text!!, R.style.Esp_Lib_Style_TextHeading6Gray);
            }

            if (app_actual_list != null && app_actual_list!!.size > 0)
                SuccessResponse()
            else
                loadDefinitionsList(textView)




            try {
                if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
                    view?.add_btn?.visibility = View.VISIBLE
                    view?.detail_text?.visibility = View.VISIBLE
                    if (mHSListener != null) {
                        mHSListener?.mAction(false)
                    }

                } else {
                    view?.add_btn?.visibility = View.GONE
                    view?.detail_text?.visibility = View.GONE
                }
            } catch (e: java.lang.Exception) {

            }
        }
        if (!searchText.isNullOrEmpty() && rlcardstack?.visibility == View.GONE) {
            view?.txtnoapplicationadded?.text = getString(R.string.esp_lib_text_norecord)
            view?.detail_text?.visibility = View.GONE
            view?.no_record_view?.setPadding(0, 300, 0, 0)
            view?.rlsearchbar?.visibility = View.VISIBLE
            view?.add_btn?.visibility = View.GONE
        }


    }

    private fun loadDefinitionsList(textView: ESP_LIB_BodyText?) {
        //start_loading_animation()
        /* APIs Mapping respective Object*/
        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
        //  def_call = apis.AllDefincations(categoryId);
        var def_call = apis.AllWithQuery()
        def_call.enqueue(object : Callback<List<ESP_LIB_CategoriesDAO>> {
            override fun onResponse(call: Call<List<ESP_LIB_CategoriesDAO>>, response: Response<List<ESP_LIB_CategoriesDAO>>) {
                // stop_loading_animation()
                if (response.body() != null && response.body().size > 0) {
                    val cat_list = ArrayList<ESP_LIB_DefinationsDAO>()
                    val body = response.body()
                    for (i in body.indices) {
                        val category = body[i].definitions
                        if (category != null) {
                            for (k in category.indices) {
                                val ESPLIBCategoryAndDefinationsDAO = category[k]
                                if (ESPLIBCategoryAndDefinationsDAO != null) {
                                    if (ESPLIBCategoryAndDefinationsDAO.isActive) {
                                        cat_list.add(ESPLIBCategoryAndDefinationsDAO)
                                    }
                                }
                            }
                        }
                    }

                    val sb = StringBuilder()
                    cat_list.forEachIndexed { index, definition ->
                        if (index < 3)
                            sb.append(". " + definition.name).append("\n")

                    }
                    textView?.setLineSpacing(1.5f, 1.5f)
                    textView?.text = sb
                    textView?.visibility = View.VISIBLE
                } else
                    textView?.visibility = View.GONE

            }

            override fun onFailure(call: Call<List<ESP_LIB_CategoriesDAO>>, t: Throwable) {
                ESP_LIB_Shared.getInstance().messageBox(t.message, context as Activity?)


            }
        })
    } //


    fun GetProfileStatus() {

        if (!ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
            stop_loading_animation()
            return
        }

        if (ESP_LIB_ESPApplication.getInstance().isComponent) {
            stop_loading_animation()
            return
        }

        start_loading_animation()

        val apis = CompRoot().getService(context);


        profile_call = apis?.userProfileStatus
        profile_call?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>?) {

                stop_loading_animation()

                if (response?.body() != null) {

                    val response_text = response.body()
                    if (response_text != null)
                        ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus = response_text

                    when {
                        response_text.equals(context?.getString(R.string.esp_lib_text_profile_complete), ignoreCase = true) -> {
                        }
                        response_text.equals(context?.getString(R.string.esp_lib_text_profile_incomplete), ignoreCase = true) -> ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc), context)
                        response_text.equals(context?.getString(R.string.esp_lib_text_profile_incomplete_admin), ignoreCase = true) -> ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc_admin), context)
                    }

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                stop_loading_animation()
            }
        })


    }


    companion object {

        var isShowingActivity: Boolean = false

        fun newInstance(): ESP_LIB_UsersApplicationsFragment {
            val fragment = ESP_LIB_UsersApplicationsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }

        fun newInstance(title: String): androidx.fragment.app.Fragment {
            val fragment = ESP_LIB_UsersApplicationsFragment()
            val args = Bundle()
            args.putString("title", title);
            fragment.arguments = args
            return fragment
        }
    }

    override fun onPause() {
        super.onPause()
        //unRegisterEventBus()
        isShowingActivity = false
        if ((mApplicationAdapterESPLIB as ESP_LIB_ListUsersApplicationsAdapterV2?)?.getPopUpMenu() != null) {
            val popUpMenu = (mApplicationAdapterESPLIB as ESP_LIB_ListUsersApplicationsAdapterV2?)?.getPopUpMenu()
            popUpMenu?.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        registerEventBus()
        isShowingActivity = true


        if (ESP_LIB_FeedbackForm.isComingFromFeedbackFrom) {
            ESP_LIB_FeedbackForm.isComingFromFeedbackFrom = false
            isEventRefreshData = true
            loadApplications(false, "")
        }

        /*   if (loadDefinitionCall != null) {
               return
           }

           when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
               true -> reLoadApplications()
               false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
           }*/

    }


    override fun deletedraftApplication(ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO) {
        //showConfirmationMessage(ESPLIBApplicationsDAO)
        popUpDialog(view, ESPLIBApplicationsDAO)
    }

    private fun postFirebaseToken() {

        // start_loading_animation()


        val firebaseTokenDAO = ESP_LIB_FirebaseTokenDAO()
        firebaseTokenDAO.fbTokenId = pref?.firebaseId!!
        firebaseTokenDAO.personaId = pref?.personaId!!
        firebaseTokenDAO.token = pref?.firebaseToken
        firebaseTokenDAO.organizationId = pref?.organizationId!!
        firebaseTokenDAO.deviceId = ESP_LIB_Shared.getInstance().getDeviceId(context)

        try {
            val apis = CompRoot().getService(context);
            var firebase_call = apis?.postFirebaseToken(firebaseTokenDAO)

            firebase_call?.enqueue(object : Callback<ESP_LIB_FirebaseTokenDAO> {
                override fun onResponse(call: Call<ESP_LIB_FirebaseTokenDAO>, response: Response<ESP_LIB_FirebaseTokenDAO>?) {
                    // stop_loading_animation()
                    if (response?.body() != null) {


                    }


                }

                override fun onFailure(call: Call<ESP_LIB_FirebaseTokenDAO>, t: Throwable) {
                    ESP_LIB_CustomLogs.displayLogs("$TAG ${t.printStackTrace()}")
                    //  stop_loading_animation()
                    //  Shared.getInstance().showAlertMessage(context.getString(R.string.error), context.getString(R.string.some_thing_went_wrong), context)

                }
            })

        } catch (ex: Exception) {

            ex.printStackTrace()
            //  stop_loading_animation()
            // Shared.getInstance().showAlertMessage(context.getString(R.string.error), context.getString(R.string.some_thing_went_wrong), context)

        }

    }
/*
    fun showConfirmationMessage(applicationDAOESPLIB: ESP_LIB_ApplicationsDAO) {


        MaterialAlertDialogBuilder(activity, R.style.Esp_Lib_Style_AlertDialogTheme)
                .setTitle(activity?.applicationContext?.getString(R.string.esp_lib_text_delete) + " " + pref?.getlabels()?.application)
                .setCancelable(false)
                .setMessage(activity?.applicationContext?.getString(R.string.esp_lib_text_areyousure) + " \"" + applicationDAOESPLIB.definitionName + " " + pref?.getlabels()?.application + "\" ?")
                .setPositiveButton(activity?.getApplicationContext()?.getString(R.string.esp_lib_text_yesdelete)) { dialogInterface, i ->
                    dialogInterface.dismiss()


                    when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                        true -> deleteApplication(applicationDAOESPLIB.id)
                        false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
                    }
                }
                .setNeutralButton(activity?.getApplicationContext()?.getString(R.string.esp_lib_text_no)) { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .show();


    }*/


    fun popUpDialog(v: View?, applicationDAOESPLIB: ESP_LIB_ApplicationsDAO) {
        val title = activity?.applicationContext?.getString(R.string.esp_lib_text_delete) + " " + pref?.getlabels()?.application
        val description = getString(R.string.esp_lib_text_areyousure) + " \"" + applicationDAOESPLIB.definitionName + " " + pref?.getlabels()?.application + "\" ?"
        val dailog = ESP_LIB_Shared.getInstance().popUpDialog(v, context, title, description)
        val btcancel = dailog.findViewById<Button>(R.id.btcancel)
        btcancel.text = getString(R.string.esp_lib_text_yesdelete)
        val btaction = dailog.findViewById<Button>(R.id.btaction)
        btaction.text = getString(R.string.esp_lib_text_no)
        val ivcross = dailog.findViewById<ImageView>(R.id.ivcross)
        btaction.setOnClickListener { v1: View? -> dailog.dismiss() }
        ivcross.setOnClickListener { v1: View? -> dailog.dismiss() }
        btcancel.setOnClickListener { v1: View? ->
            dailog.dismiss()
            when (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                true -> deleteApplication(applicationDAOESPLIB.id)
                false -> ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
            }

        }
    }

    fun deleteApplication(id: Int) {

        start_loading_animation()

        val apis = CompRoot().getService(context);
        val delete_call = apis?.deleteApplication(id)
        delete_call?.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>?) {
                reLoadApplications()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                t.printStackTrace()
                stop_loading_animation()
                ESP_LIB_Shared.getInstance().showAlertMessage(pref?.getlabels()?.application, context?.getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                return
            }
        })


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dataRefreshEvent(eventRefreshData: EventOptions.EventRefreshData) {
        isEventRefreshData = true
        reLoadApplications()

    }

    private fun registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onActionPerformed(item: Any, arraylist: Any) {
        item as ESP_LIB_ApplicationsDAO
        arraylist as ArrayList<ESP_LIB_ApplicationsDAO>

        sendDismissValue(item, arraylist)

    }

    fun sendDismissValue(item: ESP_LIB_ApplicationsDAO, mApplicationsFiltered: ArrayList<ESP_LIB_ApplicationsDAO>) {
        start_loading_dialog()
        try {

            var id = item.id
            if (item.type.equals(getString(R.string.esp_lib_text_feed), ignoreCase = true))
                id = item.parentApplicationId

            val apis = CompRoot().getService(context);
            val call = apis?.getdismissApplication(id, item.type)
            call?.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {


                    if (response.body() != null) {


                    } else {


                    }
                    mApplicationsFiltered.remove(item)
                    setCardAdapter(mApplicationsFiltered)
                    if (cardManager?.topPosition == cardAdapterESPLIB?.itemCount) {
                        paginate()
                    }


                    stop_loading_dialog()

                }


                override fun onFailure(call: Call<Any>, t: Throwable) {
                    ESP_LIB_Shared.getInstance().messageBox(context?.getString(R.string.esp_lib_text_some_thing_went_wrong), requireActivity())
                    stop_loading_dialog()

                }
            })

        } catch (ex: Exception) {
            stop_loading_dialog()

            ESP_LIB_Shared.getInstance().messageBox(context?.getString(R.string.esp_lib_text_some_thing_went_wrong), requireActivity())

        }

    }

    private fun start_loading_dialog() {

        if (!pDialog!!.isShowing())
            pDialog?.show()


    }

    private fun stop_loading_dialog() {

        if (pDialog!!.isShowing())
            pDialog?.dismiss()


    }


}// Required empty public constructor
