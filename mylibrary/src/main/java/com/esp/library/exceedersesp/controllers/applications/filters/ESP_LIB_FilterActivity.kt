package com.esp.library.exceedersesp.controllers.applications.filters


import android.os.Bundle
import android.view.View
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ActivitySubmissionRequests
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_AddApplicationCategoryAndDefinationsFragment
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.esp_lib_activity_filter.*
import kotlinx.android.synthetic.main.esp_lib_gradientcurvetoolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.esp.library.utilities.setup.applications.ESP_LIB_ListApplicationCategoryAdapter
import utilities.data.applicants.addapplication.ESP_LIB_DefinationsDAO
import utilities.interfaces.ESP_LIB_CheckFilterSelection

class ESP_LIB_FilterActivity : ESP_LIB_BaseActivity(), ESP_LIB_CheckFilterSelection {


    var mCatLayoutManager: FlexboxLayoutManager? = null
    var context: ESP_LIB_BaseActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ESP_LIB_ESPApplication.getInstance().applicationTheme)
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_filter)
        initailize()


        val isSubmissionFilter = intent.getBooleanExtra("isSubmissionFilter", false)

        if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
            if (isSubmissionFilter)
                loadSubDefinations()
            else {
                loadCategories()
            }
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext)
        }


        btapplyfilter.setOnClickListener {
            if (isSubmissionFilter)
                ESP_LIB_ActivitySubmissionRequests.subDefinationsDAOFilteredListESPLIB = tempFilterSelectionValues
            else
                ESP_LIB_AddApplicationCategoryAndDefinationsFragment.categoryAndDefinationsDAOFilteredList = tempFilterSelectionValues
            finish()
        }
        btcancel.setOnClickListener {
            if (isSubmissionFilter)
                ESP_LIB_ActivitySubmissionRequests.subDefinationsDAOFilteredListESPLIB.clear()
            else
                ESP_LIB_AddApplicationCategoryAndDefinationsFragment.categoryAndDefinationsDAOFilteredList.clear()
            finish()
        }


    }

    private fun initailize() {
        context = this@ESP_LIB_FilterActivity
        setSupportActionBar(gradientcurvetoolbar)
        supportActionBar?.title = ""
        headingtext.text = getString(R.string.esp_lib_text_filter)
        ibToolbarBack.setOnClickListener { finish() }


        mCatLayoutManager = FlexboxLayoutManager(this)
        mCatLayoutManager?.flexDirection = FlexDirection.ROW
        mCatLayoutManager?.justifyContent = JustifyContent.FLEX_START


        category_list.tag = getString(R.string.esp_lib_text_hidden)
        category_list.setHasFixedSize(true)
        category_list.layoutManager = mCatLayoutManager
        category_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
    }

    fun loadCategories() {
        val getCategoryAndDefinationsDAOFilteredList = intent.getSerializableExtra("categoryAndDefinationsDAOFilteredList") as ArrayList<*>
        start_loading_animation()
        /* APIs Mapping respective Object*/
        val apis = CompRoot().getService(bContext)
        val cat_call = apis?.AllCategories()
        cat_call?.enqueue(object : Callback<List<ESP_LIB_DefinationsDAO>> {
            override fun onResponse(call: Call<List<ESP_LIB_DefinationsDAO>>, response: Response<List<ESP_LIB_DefinationsDAO>>) {
                if (response.body() != null && response.body().isNotEmpty()) {
                    val body = response.body()
                    for (i in 0 until body.size) {
                        val categoryAndDefinationsDAO = body[i]

                        for (j in 0 until getCategoryAndDefinationsDAOFilteredList.size) {
                            val df = getCategoryAndDefinationsDAOFilteredList.get(j) as ESP_LIB_DefinationsDAO
                            if (categoryAndDefinationsDAO.id == df.id)
                                categoryAndDefinationsDAO.isChecked = true
                        }

                    }

                    val mCatAdapter = ESP_LIB_ListApplicationCategoryAdapter(body, context!!)
                    category_list.adapter = mCatAdapter
                    checkFilterSelection(body)
                    rlacceptapprove.visibility = View.VISIBLE
                    llmainlayout.visibility = View.VISIBLE


                } else
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)

                stop_loading_animation()
            }

            override fun onFailure(call: Call<List<ESP_LIB_DefinationsDAO>>, t: Throwable) {
                ESP_LIB_Shared.getInstance().messageBox(t.message, bContext)
                stop_loading_animation()

            }
        })

    }

    fun loadSubDefinations() {
        val getsubDefinitionDAOFilteredList = intent.getSerializableExtra("subDefinationsDAOFilteredList") as ArrayList<*>
        start_loading_animation()
        val apis = CompRoot().getService(bContext)
        val def_call = apis?.getSubDefinitionList()
        def_call?.enqueue(object : Callback<List<ESP_LIB_DefinationsDAO>> {
            override fun onResponse(call: Call<List<ESP_LIB_DefinationsDAO>>, response: Response<List<ESP_LIB_DefinationsDAO>>) {
                stop_loading_animation()
                if (response.body() != null && response.body().size > 0) {
                    val body = response.body()
                    val arrayListBody = ArrayList<ESP_LIB_DefinationsDAO>()//Creating an empty arraylist
                    for (i in 0 until body.size) {
                        val categoryAndDefinationsDAO = body[i]

                        for (j in 0 until getsubDefinitionDAOFilteredList.size) {
                            val df = getsubDefinitionDAOFilteredList.get(j) as ESP_LIB_DefinationsDAO
                            if (categoryAndDefinationsDAO.id == df.id)
                                categoryAndDefinationsDAO.isChecked = true
                        }


                    }

                    for (i in 0 until body.size) {
                        val getList = body.get(i);
                        val isArrayHasValue = arrayListBody.any { x -> x.id == getList.id }
                        if (!isArrayHasValue) {
                            arrayListBody.add(getList)
                        }
                    }


                    val mCatAdapter = ESP_LIB_ListApplicationCategoryAdapter(arrayListBody, context!!)
                    category_list.adapter = mCatAdapter
                    checkFilterSelection(body)
                    rlacceptapprove.visibility = View.VISIBLE
                    llmainlayout.visibility = View.VISIBLE


                } else {
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                }
            }

            override fun onFailure(call: Call<List<ESP_LIB_DefinationsDAO>>, t: Throwable) {
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext)
                stop_loading_animation()

            }
        })

    }//


    private fun start_loading_animation() {
        shimmer_view_container.visibility = View.VISIBLE
        vview.visibility = View.GONE
    }

    private fun stop_loading_animation() {
        shimmer_view_container.visibility = View.GONE
        vview.visibility = View.VISIBLE
    }

    override fun checkFilterSelection(mApplications: List<ESP_LIB_DefinationsDAO>) {

        for (i in 0 until mApplications.size) {
            val categoryAndDefinationsDAO = mApplications[i]
            if (categoryAndDefinationsDAO.isChecked) {
                btapplyfilter.isEnabled = true
                btapplyfilter.alpha = 1f
                break
            } else {
                btapplyfilter.isEnabled = false
                btapplyfilter.alpha = 0.5f
            }
        }

    }

    companion object {
        var tempFilterSelectionValues = ArrayList<ESP_LIB_DefinationsDAO>();
    }

    override fun onBackPressed() {
        super.onBackPressed()
        tempFilterSelectionValues.clear()
    }


}