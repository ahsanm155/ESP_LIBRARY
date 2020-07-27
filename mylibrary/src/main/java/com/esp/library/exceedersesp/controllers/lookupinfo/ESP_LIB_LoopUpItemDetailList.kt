package com.esp.library.exceedersesp.controllers.lookupinfo

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.lookupinfo.adapter.ESP_LIB_ListLookupInfoItemsAdapter
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_EndlessRecyclerViewScrollListener
import com.esp.library.utilities.common.ESP_LIB_Shared
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import kotlinx.android.synthetic.main.esp_lib_lookup_item_detail_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDAO
import utilities.data.lookup.ESP_LIB_LookupInfoListDetailDAO
import utilities.data.lookup.ESP_LIB_LookupInfoSearchDAO
import utilities.data.lookup.ESP_LIB_LookupValuesDAO
import java.util.*

class ESP_LIB_LoopUpItemDetailList : ESP_LIB_BaseActivity() {

    internal var TAG = javaClass.simpleName
    internal var context: ESP_LIB_BaseActivity? = null
    private var lookupItemLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var pageNo = 1
    internal var scrollTo = 0
    internal var TOTAL_RECORDS_AVAILABLE = 0

    internal var ESPLIBDynamicFormValuesArray: MutableList<ESP_LIB_DynamicFormValuesDAO> = ArrayList()
    internal var ESPLIBDynamicFormSectionDAOTempArray: MutableList<ESP_LIB_DynamicFormSectionDAO> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_lookup_item_detail_list)
        initialize()

        ESP_LIB_CustomLogs.displayLogs(TAG + " lookupid: " + intent.getIntExtra("lookupid", 0))

        getData("", false)


        etxtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(search_text: CharSequence, start: Int, before: Int, count: Int) {
               /* if (search_text.isEmpty()) {
                    refreshData()
                }*/
            }

            override fun afterTextChanged(search_text: Editable) {

            }
        })

        etxtsearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ESP_LIB_Shared.getInstance().hideKeyboard(context)
                    refreshData()
                    return true
                }
                return false
            }
        })


        swipeRefreshLayout.setOnRefreshListener {
            if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                TOTAL_RECORDS_AVAILABLE = 0
                pageNo = 1
                etxtsearch.text?.clear()
                getData("", false)
            } else {
                ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
                swipeRefreshLayout.isRefreshing = false
            }
        }


    }

    private fun refreshData() {
        pageNo = 1
        ESP_LIB_CustomLogs.displayLogs(TAG + " etxtsearch: " + etxtsearch.text.toString())
        getData(etxtsearch.text.toString(), false)
    }

    private fun addScroller() {
        lookup_item_list.addOnScrollListener(object : ESP_LIB_EndlessRecyclerViewScrollListener(lookupItemLayoutManager as androidx.recyclerview.widget.LinearLayoutManager?) {
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

                if (totalItemsCount < TOTAL_RECORDS_AVAILABLE) {
                    scrollTo = totalItemsCount

                    var searchText = etxtsearch.text.toString()
                    if (searchText.replace("\\s".toRegex(), "").length == 0)
                        searchText = ""

                    getData(searchText, true)

                    ESP_LIB_CustomLogs.displayLogs("$TAG onLoadMore totalItemsCount: $totalItemsCount")
                }

            }
        })
    }

    private fun initialize() {
        context = this as ESP_LIB_BaseActivity
        setSupportActionBar(gradienttoolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setTitle("")
        ibToolbarBack.setOnClickListener { v -> onBackPressed() }
        toolbarheading.text = intent.getStringExtra("toolbar_heading")

        lookupItemLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        lookup_item_list.setHasFixedSize(true)
        lookup_item_list.layoutManager = lookupItemLayoutManager
        lookup_item_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        val themeColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        swipeRefreshLayout?.setColorSchemeColors(themeColor, themeColor, themeColor)

    }

    private fun getData(searchString: String, isLoadMore: Boolean) {
        var searchString = searchString
        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            if (searchString.replace("\\s".toRegex(), "").length == 0)
                searchString = ""

            getLookupItemList(searchString, isLoadMore)
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext)
        }

    }

    fun getLookupItemList(searchString: String, isLoadMore: Boolean) {
        context?.let { hideKeyboard(it) }
        try {
            if (isLoadMore) {
                pageNo = pageNo + 1
                load_more_div.visibility = View.VISIBLE
            } else
                start_loading_animation()

            val lookupInfoSearchDAO = ESP_LIB_LookupInfoSearchDAO(0, intent.getIntExtra("lookupid",
                    0), pageNo, 12, searchString)


            val labels_call = ESP_LIB_Shared.getInstance().retroFitObject(context).postLookUpItems(lookupInfoSearchDAO)

            labels_call.enqueue(object : Callback<ESP_LIB_LookupInfoListDetailDAO> {
                override fun onResponse(call: Call<ESP_LIB_LookupInfoListDetailDAO>, response: Response<ESP_LIB_LookupInfoListDetailDAO>) {

                    if (response.isSuccessful && response.body() != null && response.body().totalCount > 0) {
                        if (isLoadMore) {
                            load_more_div.visibility = View.GONE
                        } else {
                            stop_loading_animation()
                            addScroller()
                        }


                        val body = response.body()
                        TOTAL_RECORDS_AVAILABLE = body.totalCount
                        populateData(body, isLoadMore)
                    } else {
                        stop_loading_animation()
                        lookup_item_list.visibility = View.GONE
                        llempty.visibility = View.VISIBLE

                    }
                }

                override fun onFailure(call: Call<ESP_LIB_LookupInfoListDetailDAO>, t: Throwable) {
                    if (isLoadMore) {
                        load_more_div.visibility = View.GONE
                    } else
                        stop_loading_animation()
                    t.printStackTrace()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            })


        } catch (ex: Exception) {
            ex.printStackTrace()
            if (isLoadMore) {
                load_more_div.visibility = View.GONE
            } else
                stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
        }

    }

    private fun populateData(body: ESP_LIB_LookupInfoListDetailDAO, isLoadMore: Boolean) {
        if (!isLoadMore) {
            ESPLIBDynamicFormValuesArray.clear()
            ESPLIBDynamicFormSectionDAOTempArray.clear()
        }

        var employeeName = ""

        val titleCustomFieldId = body.lookupTemplate.titleCustomFieldId
        val isShowEmployeeName = body.lookupTemplate.isVariable // if true the show employee name else hide it
        val sections = body.lookupTemplate.form.sections
        for (i in sections.indices) {
            var dynamicFormValuesDAOGlobal = ESP_LIB_DynamicFormValuesDAO()
            val dynamicFormSectionDAO = sections[i]
            val fields = dynamicFormSectionDAO.fields

            for (k in fields!!.indices) {
                val dynamicFormSectionFieldDAO = fields[k]
                val id = dynamicFormSectionFieldDAO.id

                if (id == titleCustomFieldId) {
                    val sectionCustomFieldId = dynamicFormSectionFieldDAO.sectionCustomFieldId
                    val items = body.items


                    for (j in items.indices) {
                        val itemsList = items[j]

                        val values = itemsList.values

                        for (h in values!!.indices) {
                            val dynamicFormValuesDAO = values[h]


                            val customFieldLookupId = dynamicFormValuesDAO.customFieldLookupId
                            val type = dynamicFormValuesDAO.type
                            if (type == 13 && customFieldLookupId == -1) {
                                employeeName = dynamicFormValuesDAO.selectedLookupText!!
                            } else
                                employeeName = ESP_LIB_ESPApplication.getInstance().user.loginResponse?.name!!
                            val valueSectionCustomFieldId = dynamicFormValuesDAO.sectionCustomFieldId

                            if (sectionCustomFieldId == valueSectionCustomFieldId) {
                                val value = dynamicFormValuesDAO.value
                                dynamicFormValuesDAO.itemid = itemsList.id
                                dynamicFormValuesDAOGlobal = dynamicFormValuesDAO
                                ESPLIBDynamicFormValuesArray.add(dynamicFormValuesDAOGlobal)
                                ESPLIBDynamicFormSectionDAOTempArray.add(dynamicFormSectionDAO)
                            }

                        }
                    }

                }
            }
        }

        try {
            var ESPLIBLookupValuesDAO: ESP_LIB_LookupValuesDAO? = null
            for (i in ESPLIBDynamicFormValuesArray.indices) {
                val formSectionValues = body.items[i].formSectionValues
                if (formSectionValues != null) {
                    val sb = StringBuilder()
                    for (j in formSectionValues.indices) {
                        val label = formSectionValues[j].label
                        val value = formSectionValues[j].value
                        sb.append(label)
                        sb.append("<:>")
                        sb.append(value)
                        sb.append("_:_")
                        ESPLIBLookupValuesDAO = ESP_LIB_LookupValuesDAO()
                        ESPLIBLookupValuesDAO.label = label
                        ESPLIBLookupValuesDAO.value = value
                    }
                    if (ESPLIBLookupValuesDAO != null)
                        ESPLIBDynamicFormValuesArray[i].filterLookup = sb.toString()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        body.lookupTemplate.form.sections = ESPLIBDynamicFormSectionDAOTempArray


        if (ESPLIBDynamicFormValuesArray.size == 0) {

            llempty.visibility = View.VISIBLE
            rlsearch.visibility = View.GONE
            swipeRefreshLayout.visibility = View.GONE

        } else {
            llempty.visibility = View.GONE
            rlsearch.visibility = View.VISIBLE
            swipeRefreshLayout.visibility = View.VISIBLE
            val listLookupInfoItemsAdapter = context?.let {
                ESP_LIB_ListLookupInfoItemsAdapter(ESPLIBDynamicFormValuesArray, body,
                        isShowEmployeeName, employeeName, it)
            }
            lookup_item_list.adapter = listLookupInfoItemsAdapter
        }

        if (isLoadMore)
            lookup_item_list.scrollToPosition(scrollTo - 3)


    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun start_loading_animation() {
        swipeRefreshLayout.isRefreshing = true

        shimmer_view_container?.visibility = View.VISIBLE
        lookup_item_list?.visibility = View.GONE

    }

    private fun stop_loading_animation() {
        swipeRefreshLayout.isRefreshing = false

        shimmer_view_container?.visibility = View.GONE
        lookup_item_list?.visibility = View.VISIBLE

    }

    override fun onBackPressed() {
        super.onBackPressed()
        context?.let { hideKeyboard(it) }
    }
}
