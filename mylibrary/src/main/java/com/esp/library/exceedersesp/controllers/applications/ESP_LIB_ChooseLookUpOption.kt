package com.esp.library.exceedersesp.controllers.applications

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.utilities.common.ESP_LIB_ProgressBarAnimation
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.customevents.EventOptions
import kotlinx.android.synthetic.main.esp_lib_activity_choose_lookup.*
import kotlinx.android.synthetic.main.esp_lib_activity_search_layout.*
import kotlinx.android.synthetic.main.esp_lib_gradientcurvetoolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import utilities.adapters.setup.applications.ESP_LIB_LookUpAdapter
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO


class ESP_LIB_ChooseLookUpOption : ESP_LIB_BaseActivity() {

    var TAG = "ChooseLookUpOption"

    internal var context: ESP_LIB_BaseActivity? = null
    private var compAdapterESPLIB: ESP_LIB_LookUpAdapter? = null
    private var mCompLayout: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var retrofit: Retrofit? = null
    internal var call: Call<List<ESP_LIB_LookUpDAO>>? = null
    var actualResponse: List<ESP_LIB_LookUpDAO>? = null


    internal var myActionMenuItem: MenuItem? = null
    internal var searchView: SearchView? = null
    internal var imm: InputMethodManager? = null
    internal var fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO? = null
    internal var anim: ESP_LIB_ProgressBarAnimation? = null

    // var lookUpItems: List<LookUpDAO>?=null;
    internal var pDialog: AlertDialog? = null

    internal var IN_LIST_RECORDS = 0
    internal var TOTAL_RECORDS_AVAILABLE = 0
    var listLoadCount: Int = 25
    internal var PER_PAGE_RECORDS = 25
    var sublist = ArrayList<ESP_LIB_LookUpDAO>()
    override fun onDestroy() {
        super.onDestroy()
        val view = currentFocus
        if (view != null) {
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_choose_lookup)
        initialize()
        setGravity()
        populateData()



        etxtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                val usersList = ArrayList<ESP_LIB_LookUpDAO>()

                if (s.isNotEmpty()) {
                    ibClear.visibility = View.VISIBLE
                } else {
                    ibClear.visibility = View.GONE
                }

                val textlength = s.length
                val searched_text = s



                if (actualResponse != null && actualResponse!!.size > 0) {
                    for (st in actualResponse!!) {
                        if (textlength <= st.name!!.length) {
                            if (st.name!!.toLowerCase().contains(searched_text)) {
                                usersList.add(st)
                            }
                        }
                    }
                }

                if (usersList.size > 0) {
                    compAdapterESPLIB = context?.let { ESP_LIB_LookUpAdapter(usersList, it, searched_text.toString(), fieldDAOESPLIB) }
                    search_list.adapter = compAdapterESPLIB
                    //  compAdapter!!.notifyDataSetChanged()
                    SuccessResponse()
                } else {
                    UnSuccessResponse()
                }


            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        ibClear.setOnClickListener {
            etxtsearch.setText("")
        }


        etxtsearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ESP_LIB_Shared.getInstance().hideKeyboard(context)
                    return true
                }
                return false
            }
        })


    }

    private fun initialize() {
        context = this@ESP_LIB_ChooseLookUpOption
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        setSupportActionBar(gradientcurvetoolbar)
        supportActionBar?.title = ""
        ibToolbarBack.setOnClickListener { finish() }

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mCompLayout = androidx.recyclerview.widget.LinearLayoutManager(context)
        search_list.setHasFixedSize(false)
        search_list.isNestedScrollingEnabled = false
        search_list.layoutManager = mCompLayout

    }

    fun populateData() {
        if (intent != null) {
            val bundle = intent?.extras
            if (bundle != null) {
                fieldDAOESPLIB = bundle.getSerializable(ESP_LIB_DynamicFormSectionFieldDAO.BUNDLE_KEY) as ESP_LIB_DynamicFormSectionFieldDAO
                if (fieldDAOESPLIB != null) {
                    headingtext.text = fieldDAOESPLIB!!.label

                    actualResponse = ESP_LIB_Shared.getInstance().getLookUpItems(fieldDAOESPLIB!!.sectionCustomFieldId)
                    if (fieldDAOESPLIB!!.allowedValuesCriteria != null && fieldDAOESPLIB!!.allowedValuesCriteria!!.isNotEmpty()) {
                        val jsonArray = JSONArray(fieldDAOESPLIB?.allowedValuesCriteria)
                        if (jsonArray.length() > 0) {
                            if (actualResponse != null && actualResponse!!.isNotEmpty()) {
                                compAdapterESPLIB = ESP_LIB_LookUpAdapter(actualResponse!!, context!!, "", fieldDAOESPLIB)
                                search_list.adapter = compAdapterESPLIB
                                SuccessResponse()
                            }
                        } else {
                            getLookups()
                        }
                    } else {
                        getLookups()
                    }

                }
            }

        }
    }

    fun refreshData() {
        if (fieldDAOESPLIB != null) {
            //toolbar_heading.text = fieldDAO!!.label
            actualResponse = ArrayList<ESP_LIB_LookUpDAO>()
            actualResponse = ESP_LIB_Shared.getInstance().getLookUpItems(fieldDAOESPLIB!!.sectionCustomFieldId)
            if (fieldDAOESPLIB!!.allowedValuesCriteria != null && fieldDAOESPLIB!!.allowedValuesCriteria!!.isNotEmpty()) {
                val jsonArray = JSONArray(fieldDAOESPLIB?.allowedValuesCriteria)
                if (jsonArray.length() > 0) {
                    if (actualResponse != null && actualResponse!!.isNotEmpty()) {
                        compAdapterESPLIB = ESP_LIB_LookUpAdapter(actualResponse!!, context!!, "", fieldDAOESPLIB)
                        search_list.adapter = compAdapterESPLIB
                        SuccessResponse()
                    }
                } else {
                    getLookups()
                }
            } else {
                getLookups()
            }
        }
    }

    fun getLookups() {
        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            GetLoadLookUps(fieldDAOESPLIB!!.lookUpId)
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext)
        }
    }

    private fun updateData() {
        if (IN_LIST_RECORDS < TOTAL_RECORDS_AVAILABLE) {
            if (actualResponse != null && actualResponse!!.size > 0) {
                listLoadCount += PER_PAGE_RECORDS
                if (actualResponse!!.size < listLoadCount)
                    listLoadCount = actualResponse!!.size
                sublist.addAll(actualResponse?.subList(IN_LIST_RECORDS, listLoadCount)!!)
                IN_LIST_RECORDS = listLoadCount
                compAdapterESPLIB?.notifyDataSetChanged()
            }
        }
    }


    private fun start_loading_animation() {
        try {
            if (!pDialog!!.isShowing)
                pDialog!!.show()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun stop_loading_animation() {
        try {
            if (pDialog!!.isShowing)
                pDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun SuccessResponse() {
        search_list.visibility = View.VISIBLE
        etxtsearch.visibility = View.VISIBLE
        no_results_available_div.visibility = View.GONE
    }

    private fun UnSuccessResponse() {
        search_list.visibility = View.GONE
        no_results_available_div.visibility = View.VISIBLE
    }

    fun GetLoadLookUps(id: Int?) {


        start_loading_animation()

        try {

            call = ESP_LIB_Shared.getInstance().retroFitObject(context).Lookups(id)

            call!!.enqueue(object : Callback<List<ESP_LIB_LookUpDAO>> {
                override fun onResponse(call: Call<List<ESP_LIB_LookUpDAO>>, response: Response<List<ESP_LIB_LookUpDAO>>) {

                    stop_loading_animation()
                    if (response.body() != null && response.body().size > 0) {
                        actualResponse = response.body()

                        if (actualResponse!!.size < PER_PAGE_RECORDS)
                            PER_PAGE_RECORDS = actualResponse!!.size

                        sublist.addAll(actualResponse?.subList(0, PER_PAGE_RECORDS)!!)


                        if (actualResponse != null && actualResponse!!.size > 0) {


                            //    compAdapter!!.notifyDataSetChanged()
                            IN_LIST_RECORDS = sublist.size
                            TOTAL_RECORDS_AVAILABLE = response.body().size

                            compAdapterESPLIB = ESP_LIB_LookUpAdapter(actualResponse!!, context!!, "", fieldDAOESPLIB)
                            search_list.adapter = compAdapterESPLIB
                            etxtsearch.visibility = View.VISIBLE
                            SuccessResponse()
                            //addScroller()

                        } else {
                            etxtsearch.visibility = View.GONE
                            UnSuccessResponse()
                        }

                    } else {
                        etxtsearch.visibility = View.GONE
                        UnSuccessResponse()
                    }

                }

                override fun onFailure(call: Call<List<ESP_LIB_LookUpDAO>>, t: Throwable) {
                    etxtsearch.visibility = View.GONE
                    stop_loading_animation()
                    UnSuccessResponse()
                }
            })

        } catch (ex: Exception) {
            etxtsearch.visibility = View.GONE
            stop_loading_animation()
            UnSuccessResponse()

        }

    }//LoggedInUser end

    private fun setGravity() {
        val pref = ESP_LIB_SharedPreference(context)
        if (pref.language.equals("ar", ignoreCase = true)) {
            headingtext.gravity = Gravity.RIGHT
            card_error_text.gravity = Gravity.RIGHT

        } else {
            headingtext.gravity = Gravity.LEFT
            card_error_text.gravity = Gravity.LEFT
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

    companion object {

        var isOpen: Boolean = false
        var ACTIVITY_NAME = "controllers.applications.ChooseLookUpOption"
    }

    override fun onResume() {
        super.onResume()
        isOpen = true


    }

    override fun onPause() {
        super.onPause()
        isOpen = false
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dataRefreshEvent(eventTriggerController: EventOptions.EventTriggerController) {
        stop_loading_animation()
        refreshData()
    }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


}
