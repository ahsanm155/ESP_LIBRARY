package com.esp.library.exceedersesp.controllers.applications

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.applications.filters.ESP_LIB_FilterActivity
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import kotlinx.android.synthetic.main.esp_lib_activity_search_layout.*
import kotlinx.android.synthetic.main.esp_lib_activity_submission_requests.*
import kotlinx.android.synthetic.main.esp_lib_fragment_users_applications.view.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.ESP_LIB_FilterItemsAdapter
import utilities.adapters.setup.applications.ESP_LIB_ListApplicationSubDefinationAdapter
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO
import utilities.interfaces.ESP_LIB_DeleteFilterListener
import java.util.*
import kotlin.collections.ArrayList

class ESP_LIB_ActivitySubmissionRequests : ESP_LIB_BaseActivity(), ESP_LIB_DeleteFilterListener {

    internal var context: Context? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var filter_definition_list: ArrayList<ESP_LIB_CategoryAndDefinationsDAO> = ArrayList()
    internal var definition_list: ArrayList<ESP_LIB_CategoryAndDefinationsDAO> = ArrayList()
    private var mDefAdapterESPLIB: ESP_LIB_ListApplicationSubDefinationAdapter? = null
    internal var ESPLIBFilter_adapter: ESP_LIB_FilterItemsAdapter? = null


    companion object {

        var ACTIVITY_NAME = javaClass.simpleName
        var subDefinationsDAOFilteredListESPLIB: ArrayList<ESP_LIB_CategoryAndDefinationsDAO> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_submission_requests)
        initailize()

        val subDefinitionBody = intent.getSerializableExtra("subDefinitionBody")
        if (subDefinitionBody==null)
            loadData()
        else
        {
            start_loading_animation()
            val handler = Handler()
            handler.postDelayed({
                stop_loading_animation()
                populateData(subDefinitionBody as ArrayList<ESP_LIB_CategoryAndDefinationsDAO>)
            }, 500)

        }

        ivfilter?.setOnClickListener { view ->
            val i = Intent(this, ESP_LIB_FilterActivity::class.java)
            i.putExtra("subDefinationsDAOFilteredList", subDefinationsDAOFilteredListESPLIB)
            i.putExtra("isSubmissionFilter", true)
            startActivity(i)
        }

        etxtsearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.isEmpty()) {
                    ibClear.visibility=View.GONE
                    mDefAdapterESPLIB = ESP_LIB_ListApplicationSubDefinationAdapter(filter_definition_list, context!!, sub_defination_list)
                    sub_defination_list?.adapter = mDefAdapterESPLIB

                } else {
                    mDefAdapterESPLIB?.filter?.filter(s)
                    ibClear.visibility=View.VISIBLE
                }


            }

            override fun afterTextChanged(s: Editable) {
                listcount?.text = mDefAdapterESPLIB?.itemCount.toString() + " " + pref?.getlabels()?.submissionRequests
            }
        })

        etxtsearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ESP_LIB_Shared.getInstance().hideKeyboard(context as Activity?)
                    return true
                }
                return false
            }
        })

        ibClear.setOnClickListener {
            etxtsearch.setText("")
        }


        swipeRefreshLayout?.setOnRefreshListener { loadData() }

    }


    private fun initailize() {
        context = this@ESP_LIB_ActivitySubmissionRequests
        pref = ESP_LIB_SharedPreference(context)
        setSupportActionBar(gradienttoolbar)
        ibToolbarBack?.setOnClickListener { onBackPressed() }

        val mDefLayoutManager = LinearLayoutManager(this)
        sub_defination_list?.setHasFixedSize(true)
        sub_defination_list?.layoutManager = mDefLayoutManager
        sub_defination_list?.itemAnimator = DefaultItemAnimator()
        sub_defination_list?.isNestedScrollingEnabled = true

        sub_defination_list?.setOnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            v.onTouchEvent(event)
            true
        }

        toolbarheading?.text = pref?.getlabels()?.submissionRequests
        filter_horizontal_list?.setHasFixedSize(true)
        filter_horizontal_list?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filter_horizontal_list?.itemAnimator = DefaultItemAnimator()

        val themeColor = ContextCompat.getColor(context!!,R.color.colorPrimaryDark)
        swipeRefreshLayout?.setColorSchemeColors(themeColor, themeColor, themeColor)

    }

    fun loadData() {
        if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == ESP_LIB_Enums.applicant.toString()) {
            if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
                subDefinationsDAOFilteredListESPLIB.clear()
                filter_horizontal_list?.visibility=View.GONE
                etxtsearch?.setText("")
                ivfilter?.setBackgroundResource(R.drawable.esp_lib_drawable_ic_filter_gray)
                getSubDefinations()
            } else {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext)
            }
        }
    }

    fun getSubDefinations() {

        start_loading_animation()

        val apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext)
        val def_call = apis.getSubDefinitionList()
        def_call.enqueue(object : Callback<List<ESP_LIB_CategoryAndDefinationsDAO>> {
            @SuppressLint("RestrictedApi")
            override fun onResponse(call: Call<List<ESP_LIB_CategoryAndDefinationsDAO>>, response: Response<List<ESP_LIB_CategoryAndDefinationsDAO>>) {

                if (response.body() != null && response.body().size > 0) {
                    val body = response.body()
                    populateData(body)

                } else {
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                }

                stop_loading_animation()
            }

            override fun onFailure(call: Call<List<ESP_LIB_CategoryAndDefinationsDAO>>, t: Throwable) {
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                stop_loading_animation()
            }
        })

    }

    private fun populateData(body: List<ESP_LIB_CategoryAndDefinationsDAO>) {
        definition_list.clear()
        filter_definition_list.clear()
        definition_list.addAll(body)
        filter_definition_list.addAll(definition_list)
        mDefAdapterESPLIB = ESP_LIB_ListApplicationSubDefinationAdapter(filter_definition_list, context!!, sub_defination_list)
        sub_defination_list?.adapter = mDefAdapterESPLIB
        listcount?.text = mDefAdapterESPLIB?.getItemCount().toString() + " " + pref?.getlabels()?.submissionRequests
        toolbarheading?.text = pref?.getlabels()?.submissionRequests + " (" + body.size + ")"
    }

    override fun deleteFilters(filtersList: ESP_LIB_CategoryAndDefinationsDAO) {
        if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
            if (ESPLIBFilter_adapter != null) {
                subDefinationsDAOFilteredListESPLIB.remove(filtersList)
                ESPLIBFilter_adapter?.notifyDataSetChanged()
                populateFilters()
            }
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext)
        }
    }

    private fun populateFilters() {
        filter_definition_list.clear()
        if (subDefinationsDAOFilteredListESPLIB.size > 0) {
            ivfilter?.setBackgroundResource(R.drawable.esp_lib_drawable_ic_filter_green)
           // ivfilter?.setColorFilter(ContextCompat.getColor(context!!, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
            filter_horizontal_list?.visibility = View.VISIBLE

        } else {
            ivfilter?.setBackgroundResource(R.drawable.esp_lib_drawable_ic_filter_gray)
            filter_horizontal_list?.visibility = View.GONE
            filter_definition_list.addAll(definition_list)
        }

        val categoriesIds = ArrayList<Int>()
        for (i in subDefinationsDAOFilteredListESPLIB.indices) {
            val df = subDefinationsDAOFilteredListESPLIB.get(i) as ESP_LIB_CategoryAndDefinationsDAO
            categoriesIds.add(df.id)

            for (h in definition_list.indices) {
                if (definition_list.get(h).id == df.id) {
                    filter_definition_list.add(definition_list.get(h))
                }
            }

        }

        mDefAdapterESPLIB = ESP_LIB_ListApplicationSubDefinationAdapter(filter_definition_list, context!!, sub_defination_list)
        sub_defination_list?.adapter = mDefAdapterESPLIB
        listcount?.text = mDefAdapterESPLIB?.itemCount.toString() + " " + pref?.getlabels()?.submissionRequests

    }

    private fun start_loading_animation() {
        shimmer_view_container?.visibility=View.VISIBLE
    }

    private fun stop_loading_animation() {

        val handler = Handler()
        handler.postDelayed({
            swipeRefreshLayout.isRefreshing = false
            shimmer_view_container?.visibility=View.GONE
        }, 1500)


    }


    override fun onResume() {
        super.onResume()

        ESPLIBFilter_adapter = ESP_LIB_FilterItemsAdapter(subDefinationsDAOFilteredListESPLIB, context!!)
        ESPLIBFilter_adapter?.setActivitContext(this)
        filter_horizontal_list?.adapter = ESPLIBFilter_adapter
        populateFilters()
        etxtsearch.setText("")

        if (ESP_LIB_ESPApplication.getInstance().isGoToMainScreen) {
            ESP_LIB_ESPApplication.getInstance().isGoToMainScreen = false
            onBackPressed()
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
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        subDefinationsDAOFilteredListESPLIB.clear()
    }
}